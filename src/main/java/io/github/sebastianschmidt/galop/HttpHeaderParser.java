package io.github.sebastianschmidt.galop;

import java.io.*;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

/**
 *
 */
public class HttpHeaderParser {

    private static final Charset HTTP_HEADER_CHARSET = Charset.forName("ASCII");
    private static final String HEADER_CONTENT_LENGTH_PREFIX = "Content-Length:";

    private final int headerSizeLimit;

    public HttpHeaderParser(final int headerSizeLimit) {

        if (headerSizeLimit < 255) {
            throw new IllegalArgumentException("headerSizeLimit must be at least 255 bytes.");
        }

        this.headerSizeLimit = headerSizeLimit;

    }

    public int getHeaderSizeLimit() {
        return headerSizeLimit;
    }

    public long calculateRequestLength(final InputStream inputStream) throws IOException {

        requireNonNull(inputStream, "inputStream must not be null.");

        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark.");
        }

        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, headerSizeLimit);

        inputStream.mark(headerSizeLimit);

        try {
            return parseRequest(limitedInputStream);
        } finally {
            inputStream.reset();
        }

    }

    private long parseRequest(final LimitedInputStream limitedInputStream) throws IOException {

        byte[] bytes = new byte[headerSizeLimit];
        int currentByteIndex = 0;
        int currentByte;

        long contentLength = 0;

        while ((currentByte = limitedInputStream.read()) > -1) {

            char currentChar = (char) currentByte;

            if (currentChar == '\n') {

                if (currentByteIndex != 0) {

                    final String line = new String(bytes, 0, currentByteIndex, HTTP_HEADER_CHARSET);
                    currentByteIndex = 0;

                    if (line.startsWith(HEADER_CONTENT_LENGTH_PREFIX)) {

                        final String contentLengthString = line.substring(HEADER_CONTENT_LENGTH_PREFIX.length()).trim();

                        try {
                            contentLength = Long.parseLong(contentLengthString);
                        } catch (final NumberFormatException ex) {
                            throw new InvalidHttpHeaderException("Invalid Content-Length: Not a valid number.");
                        }

                        if (contentLength < 0) {
                            throw new InvalidHttpHeaderException("Invalid Content-Length: Must be greater or equal to 0.");
                        }
                    }

                } else {
                    break;
                }

            } else if (currentChar != '\r') {
                bytes[currentByteIndex] = (byte) currentByte;
                currentByteIndex++;
            }

        }

        return limitedInputStream.getTotalNumberOfBytesRead() + contentLength;

    }

}
