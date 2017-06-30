package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.HttpConstants.HEADER_CHARSET;
import static io.github.galop_proxy.galop.http.HttpConstants.HTTP_VERSION_PREFIX;

final class MessageParserImpl implements MessageParser {

    private final HttpHeaderRequestConfiguration requestConfiguration;
    private final HttpHeaderResponseConfiguration responseConfiguration;

    @Inject
    MessageParserImpl(final HttpHeaderRequestConfiguration requestConfiguration,
                      final HttpHeaderResponseConfiguration responseConfiguration) {
        this.requestConfiguration = checkNotNull(requestConfiguration, "requestConfiguration");
        this.responseConfiguration = checkNotNull(responseConfiguration, "responseConfiguration");
    }

    // Parse request:

    @Override
    public Request parseRequest(final InputStream inputStream, final Runnable startParsingCallback) throws IOException {

        checkNotNull(inputStream, "inputStream");

        final int maxHttpHeaderSize = getMaxHttpHeaderSize(true);
        final byte[] buffer = new byte[maxHttpHeaderSize];
        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, maxHttpHeaderSize);

        final Request request = parseRequestLine(limitedInputStream, buffer, startParsingCallback);
        parseHeaderFields(request, limitedInputStream, buffer, true);
        return request;

    }

    private Request parseRequestLine(final InputStream inputStream, final byte[] buffer,
                                     final Runnable startParsingCallback) throws IOException {

        final String[] requestLine = getNextLine(inputStream, buffer, startParsingCallback).split(" ");

        if (requestLine.length != 3) {
            throw new InvalidHttpHeaderException("Invalid request line.");
        }

        final String method = requestLine[0];
        final String requestTarget = requestLine[1];
        final Version version = parseVersion(requestLine[2]);

        return new RequestImpl(version, method, requestTarget);

    }

    // Parse Response:

    @Override
    public Response parseResponse(final InputStream inputStream, final Runnable startParsingCallback) throws IOException {

        checkNotNull(inputStream, "inputStream");

        final int maxHttpHeaderSize = getMaxHttpHeaderSize(false);
        final byte[] buffer = new byte[maxHttpHeaderSize];
        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, maxHttpHeaderSize);

        final Response response = parseStatusLine(limitedInputStream, buffer, startParsingCallback);
        parseHeaderFields(response, limitedInputStream, buffer, false);
        return response;

    }

    private Response parseStatusLine(final InputStream inputStream, final byte[] buffer,
                                     final Runnable startParsingCallback) throws IOException {

        final String[] statusLine = getNextLine(inputStream, buffer, startParsingCallback).split(" ");

        if (statusLine.length != 3 && statusLine.length != 2) {
            throw new InvalidHttpHeaderException("Invalid status line.");
        }

        final Version version = parseVersion(statusLine[0]);
        final int statusCode = parseStatusCode(statusLine[1]);
        final String reasonPhrase = parseReasonPhrase(statusLine);

        return new ResponseImpl(version, statusCode, reasonPhrase);

    }

    private int parseStatusCode(final String statusCode) throws InvalidHttpHeaderException {

        if (statusCode.length() != 3
                || !Character.isDigit(statusCode.charAt(0))
                || !Character.isDigit(statusCode.charAt(1))
                || !Character.isDigit(statusCode.charAt(2))) {
            throw new InvalidHttpHeaderException("Invalid status line: The status code must consist of three digits.");
        }

        return Integer.parseInt(statusCode);

    }

    private String parseReasonPhrase(final String[] statusLine) {

        if (statusLine.length == 3) {
            return statusLine[2];
        } else {
            return "";
        }

    }

    // Common methods:

    private int getMaxHttpHeaderSize(final boolean request) {
        if (request) {
            return requestConfiguration.getMaxSize();
        } else {
            return responseConfiguration.getMaxSize();
        }
    }

    private String getNextLine(final InputStream inputStream, final byte[] buffer) throws IOException {
        return getNextLine(inputStream, buffer, null);
    }

    private String getNextLine(final InputStream inputStream, final byte[] buffer, final Runnable startParsingCallback)
            throws IOException {

        int currentByteIndex = 0;
        boolean carriageReturn = false;
        boolean lineBreak = false;

        boolean firstByte = true;

        int currentByte;

        while ((currentByte = inputStream.read()) > -1) {

            if (firstByte) {
                firstByte = false;
                handleFirstByte(startParsingCallback);
            }

            buffer[currentByteIndex] = (byte) currentByte;
            currentByteIndex++;

            final char currentChar = (char) currentByte;

            if (currentChar == '\r') {
                carriageReturn = true;
            } else if (carriageReturn && currentChar == '\n') {
                lineBreak = true;
                break;
            } else {
                carriageReturn = false;
            }

        }

        if (lineBreak) {
            // Because the carriage return and the line break should be omitted.
            currentByteIndex = currentByteIndex - 2;
        }

        return new String(buffer, 0, currentByteIndex, HEADER_CHARSET);

    }

    private void handleFirstByte(final Runnable startParsingCallback) {
        if (startParsingCallback != null) {
            startParsingCallback.run();
        }
    }

    private Version parseVersion(final String version) throws InvalidHttpHeaderException {

        // "HTTP/" DIGIT "." DIGIT
        if (version.length() != 8
                || !version.startsWith(HTTP_VERSION_PREFIX)
                || !Character.isDigit(version.charAt(5))
                || version.charAt(6) != '.'
                || !Character.isDigit(version.charAt(7))) {
            throw new InvalidHttpHeaderException("Invalid HTTP version in request line.");
        }

        final int major = Character.getNumericValue(version.charAt(5));
        final int minor = Character.getNumericValue(version.charAt(7));

        return new Version(major, minor);

    }

    private void parseHeaderFields(final Message message, final InputStream inputStream, final byte[] buffer,
                                   final boolean request) throws IOException {

        String line;

        while (!(line = getNextLine(inputStream, buffer)).isEmpty()) {
            parseHeaderField(message, line, request);
        }

    }

    private void parseHeaderField(final Message message, final String line, final boolean request)
            throws InvalidHttpHeaderException {

        final int colonIndex = line.indexOf(':');

        if (colonIndex == -1) {
            throw new InvalidHttpHeaderException("Invalid HTTP header field: Missing colon.");
        }

        String name = line.substring(0, colonIndex).toLowerCase(Locale.ENGLISH);

        if (name.isEmpty()) {
            throw new InvalidHttpHeaderException("Invalid HTTP header field: Empty header field name.");
        }

        if (Character.isWhitespace(name.charAt(name.length() - 1))) {

            if (request) {
                throw new InvalidHttpHeaderException("Invalid HTTP header field: "
                        + "No whitespace is allowed between the header field-name and colon.");
            } else {
                name = StringUtils.stripEnd(name, null);
            }

        }

        final String value;

        if (colonIndex + 1 < line.length()) {
            value = line.substring(colonIndex + 1).trim();
        } else {
            value = "";
        }

        if (!message.getHeaderFields().containsKey(name)) {
            final List<String> values = new ArrayList<>();
            values.add(value);
            message.getHeaderFields().put(name, values);
        } else {
            message.getHeaderFields().get(name).add(value);
        }

    }

}
