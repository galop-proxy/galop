package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;

import javax.inject.Inject;
import java.io.*;
import java.util.Locale;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class HttpHeaderParserImpl implements HttpHeaderParser {

    private static final class ResultImpl implements Result {

        private final boolean chunkedTransferEncoding;
        private final long headerLength;
        private final Long totalLength;

        private ResultImpl(final boolean chunkedTransferEncoding, final long headerLength, final Long totalLength) {
            this.chunkedTransferEncoding = chunkedTransferEncoding;
            this.headerLength = headerLength;
            this.totalLength = totalLength;
        }

        @Override
        public boolean isChunkedTransferEncoding() {
            return chunkedTransferEncoding;
        }

        @Override
        public long getHeaderLength() {
            return headerLength;
        }

        @Override
        public Long getTotalLength() {
            return totalLength;
        }

    }

    private final HttpHeaderRequestConfiguration requestConfiguration;
    private final HttpHeaderResponseConfiguration responseConfiguration;

    @Inject
    HttpHeaderParserImpl(final HttpHeaderRequestConfiguration requestConfiguration,
                         final HttpHeaderResponseConfiguration responseConfiguration) {
        this.requestConfiguration = checkNotNull(requestConfiguration, "requestConfiguration");
        this.responseConfiguration = checkNotNull(responseConfiguration, "responseConfiguration");
    }

    @Override
    public Result parse(final InputStream inputStream, final boolean request) throws IOException {
        return parse(inputStream, request, null);
    }

    @Override
    public Result parse(final InputStream inputStream, final boolean request, final Runnable startParsingCallback)
            throws IOException {

        checkNotNull(inputStream, "inputStream");

        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark.");
        }

        final int maxHttpHeaderSize = getMaxHttpHeaderSize(request);
        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, maxHttpHeaderSize);
        inputStream.mark(maxHttpHeaderSize);

        final Result result = parseHeader(limitedInputStream, maxHttpHeaderSize, startParsingCallback);
        inputStream.reset();
        return result;

    }

    private int getMaxHttpHeaderSize(final boolean request) {
        if (request) {
            return requestConfiguration.getMaxSize();
        } else {
            return responseConfiguration.getMaxSize();
        }
    }

    private Result parseHeader(final LimitedInputStream limitedInputStream, final int maxHttpHeaderSize,
                               final Runnable startParsingCallback) throws IOException {

        boolean firstByte = true;

        byte[] bytes = new byte[maxHttpHeaderSize];
        int currentByteIndex = 0;
        int currentByte;

        long contentLength = 0;
        boolean chunkedEncoding = false;

        while ((currentByte = limitedInputStream.read()) > -1) {

            if (firstByte) {

                firstByte = false;

                if (startParsingCallback != null) {
                    startParsingCallback.run();
                }

            }

            char currentChar = (char) currentByte;

            if (currentChar == '\n') {

                if (currentByteIndex != 0) {

                    final String line = new String(bytes, 0, currentByteIndex, HttpConstants.HEADER_CHARSET)
                            .toLowerCase(Locale.ENGLISH);

                    currentByteIndex = 0;

                    if (line.startsWith(HttpConstants.HEADER_CONTENT_LENGTH_PREFIX)) {
                        contentLength = parseContentLength(line);
                    } else if (line.startsWith(HttpConstants.HEADER_TRANSFER_ENCODING_PREFIX)) {
                        chunkedEncoding = parseTransferEncoding(line);
                    }

                } else {
                    break;
                }

            } else if (currentChar != '\r') {
                bytes[currentByteIndex] = (byte) currentByte;
                currentByteIndex++;
            }

        }

        final long headerLength = limitedInputStream.getTotalNumberOfBytesRead();
        final Long totalLength;

        if (chunkedEncoding) {
            totalLength = null;
        } else {
            totalLength = headerLength + contentLength;
        }

        return new ResultImpl(chunkedEncoding, headerLength, totalLength);

    }

    private long parseContentLength(final String line) throws IOException {

        final String contentLengthString = line.substring(HttpConstants.HEADER_CONTENT_LENGTH_PREFIX.length()).trim();

        final long contentLength;

        try {
            contentLength = Long.parseLong(contentLengthString);
        } catch (final NumberFormatException ex) {
            throw new InvalidHttpHeaderException("Invalid Content-Length: Not a valid number.");
        }

        if (contentLength < 0) {
            throw new InvalidHttpHeaderException("Invalid Content-Length: Must be greater or equal to 0.");
        }

        return contentLength;

    }

    private boolean parseTransferEncoding(final String line) throws IOException {

        final String transferEncoding = line.substring(HttpConstants.HEADER_TRANSFER_ENCODING_PREFIX.length()).trim();

        switch (transferEncoding) {
            case HttpConstants.TRANSFER_ENCODING_CHUNKED:
                return true;
            case HttpConstants.TRANSFER_ENCODING_IDENTITY:
                return false;
            default:
                throw new UnsupportedTransferEncodingException(transferEncoding);
        }

    }

}
