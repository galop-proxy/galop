package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
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

final class MessageParserImpl implements MessageParser {

    private final HttpHeaderRequestConfiguration requestConfiguration;
    private final HttpHeaderResponseConfiguration responseConfiguration;
    private final StartLineParser startLineParser;

    @Inject
    MessageParserImpl(final HttpHeaderRequestConfiguration requestConfiguration,
                      final HttpHeaderResponseConfiguration responseConfiguration,
                      final StartLineParser startLineParser) {
        this.requestConfiguration = checkNotNull(requestConfiguration, "requestConfiguration");
        this.responseConfiguration = checkNotNull(responseConfiguration, "responseConfiguration");
        this.startLineParser = checkNotNull(startLineParser, "startLineParser");
    }

    // Parse request:

    @Override
    public Request parseRequest(final InputStream inputStream, final Runnable startParsingCallback) throws IOException {

        checkNotNull(inputStream, "inputStream");

        final int maxHttpHeaderSize = getMaxHttpHeaderSize(true);
        final byte[] buffer = new byte[maxHttpHeaderSize];
        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, maxHttpHeaderSize);

        final Request request = startLineParser.parseRequestLine(
                () -> getNextLine(limitedInputStream, buffer, startParsingCallback));
        parseHeaderFields(request, limitedInputStream, buffer, true);
        return request;

    }

    // Parse Response:

    @Override
    public Response parseResponse(final InputStream inputStream, final Runnable startParsingCallback) throws IOException {

        checkNotNull(inputStream, "inputStream");

        final int maxHttpHeaderSize = getMaxHttpHeaderSize(false);
        final byte[] buffer = new byte[maxHttpHeaderSize];
        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, maxHttpHeaderSize);

        final Response response = startLineParser.parseStatusLine(
                () -> getNextLine(limitedInputStream, buffer, startParsingCallback));
        parseHeaderFields(response, limitedInputStream, buffer, false);
        return response;

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
            // The carriage return and the line break should be omitted.
            currentByteIndex = currentByteIndex - 2;
        }

        return new String(buffer, 0, currentByteIndex, HEADER_CHARSET);

    }

    private void handleFirstByte(final Runnable startParsingCallback) {

        if (startParsingCallback != null) {
            startParsingCallback.run();
        }

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

        final int colonIndex = splitHeaderFieldLine(line);

        final String name = parseHeaderFieldName(line, colonIndex, request);
        final String value = parseHeaderFieldValue(line, colonIndex);

        addHeaderFieldToMessage(message, name, value);

    }

    private int splitHeaderFieldLine(final String line) throws InvalidHttpHeaderException {

        final int colonIndex = line.indexOf(':');

        if (colonIndex == -1) {
            throw new InvalidHttpHeaderException("Invalid HTTP header field: Missing colon.");
        }

        return colonIndex;

    }

    private String parseHeaderFieldName(final String line, final int colonIndex, final boolean request)
            throws InvalidHttpHeaderException {

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

        return name;

    }

    private String parseHeaderFieldValue(final String line, final int colonIndex) {

        final String value;

        if (colonIndex + 1 < line.length()) {
            value = line.substring(colonIndex + 1).trim();
        } else {
            value = "";
        }

        return value;

    }

    private void addHeaderFieldToMessage(final Message message, final String name, final String value) {

        if (!message.getHeaderFields().containsKey(name)) {
            final List<String> values = new ArrayList<>();
            values.add(value);
            message.getHeaderFields().put(name, values);
        } else {
            message.getHeaderFields().get(name).add(value);
        }

    }

}
