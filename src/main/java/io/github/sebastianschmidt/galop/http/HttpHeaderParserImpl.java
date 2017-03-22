package io.github.sebastianschmidt.galop.http;

import io.github.sebastianschmidt.galop.commons.LimitedInputStream;

import java.io.*;

import static io.github.sebastianschmidt.galop.http.HttpConstants.*;
import static java.util.Objects.requireNonNull;

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

    @Override
    public Result parse(final InputStream inputStream, final int maxHttpHeaderSize) throws IOException {
        return parse(inputStream, maxHttpHeaderSize, null);
    }

    @Override
    public Result parse(final InputStream inputStream, final int maxHttpHeaderSize,
                        final Runnable startParsingCallback) throws IOException {

        requireNonNull(inputStream, "inputStream must not be null.");

        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark.");
        }

        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, maxHttpHeaderSize);
        inputStream.mark(maxHttpHeaderSize);

        final Result result = parseRequest(limitedInputStream, maxHttpHeaderSize, startParsingCallback);
        inputStream.reset();
        return result;

    }

    private Result parseRequest(final LimitedInputStream limitedInputStream, final int maxHttpHeaderSize,
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

                    final String line = new String(bytes, 0, currentByteIndex, HEADER_CHARSET).toLowerCase();
                    currentByteIndex = 0;

                    if (line.startsWith(HEADER_CONTENT_LENGTH_PREFIX)) {
                        contentLength = parseContentLength(line);
                    } else if (line.startsWith(HEADER_TRANSFER_ENCODING_PREFIX)) {
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

        final String contentLengthString = line.substring(HEADER_CONTENT_LENGTH_PREFIX.length()).trim();

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

        final String transferEncoding = line.substring(HEADER_TRANSFER_ENCODING_PREFIX.length()).trim();

        switch (transferEncoding) {
            case TRANSFER_ENCODING_CHUNKED:
                return true;
            case TRANSFER_ENCODING_IDENTITY:
                return false;
            default:
                throw new UnsupportedTransferEncodingException(transferEncoding);
        }

    }

}
