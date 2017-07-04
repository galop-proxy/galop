package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.Constants.HEADER_CHARSET;

final class MessageParserImpl implements MessageParser {

    private final HttpHeaderRequestConfiguration requestConfiguration;
    private final HttpHeaderResponseConfiguration responseConfiguration;
    private final StartLineParser startLineParser;
    private final HeaderParser headerParser;

    @Inject
    MessageParserImpl(final HttpHeaderRequestConfiguration requestConfiguration,
                      final HttpHeaderResponseConfiguration responseConfiguration,
                      final StartLineParser startLineParser, final HeaderParser headerParser) {
        this.requestConfiguration = checkNotNull(requestConfiguration, "requestConfiguration");
        this.responseConfiguration = checkNotNull(responseConfiguration, "responseConfiguration");
        this.startLineParser = checkNotNull(startLineParser, "startLineParser");
        this.headerParser = checkNotNull(headerParser, "headerParser");
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
        return startLineParser.parseRequestLine(() -> getNextLine(inputStream, buffer, startParsingCallback));
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
        return startLineParser.parseStatusLine(() -> getNextLine(inputStream, buffer, startParsingCallback));
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

        final Callable<String, IOException> nextLine = () -> getNextLine(inputStream, buffer);

        final Map<String, List<String>> headerFields;

        if (request) {
            headerFields = headerParser.parseRequestHeaders(nextLine);
        } else {
            headerFields = headerParser.parseResponseHeaders(nextLine);
        }

        message.getHeaderFields().putAll(headerFields);

    }

}
