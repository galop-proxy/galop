package io.github.sebastianschmidt.galop.parser;

import io.github.sebastianschmidt.galop.LimitedInputStream;

import java.io.*;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

public class HttpHeaderParser {

    private static final Charset HTTP_HEADER_CHARSET = Charset.forName("ASCII");
    private static final String HEADER_CONTENT_LENGTH_PREFIX = "Content-Length:";
    private static final String HEADER_TRANSFER_ENCODING_PREFIX = "Transfer-Encoding:";
    private static final String IDENTITY_TRANSFER_ENCODING = "identity";

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

    public long calculateTotalLength(final InputStream inputStream) throws IOException {

        requireNonNull(inputStream, "inputStream must not be null.");

        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark.");
        }

        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, headerSizeLimit);
        inputStream.mark(headerSizeLimit);

        final long calculatedLength = parseRequest(limitedInputStream);
        inputStream.reset();
        return calculatedLength;

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
                        contentLength = parseContentLength(line);
                    } else if (line.startsWith(HEADER_TRANSFER_ENCODING_PREFIX)) {
                        parseTransferEncoding(line);
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

    private void parseTransferEncoding(final String line) throws IOException {

        final String transferEncoding = line.substring(HEADER_TRANSFER_ENCODING_PREFIX.length()).trim();

        if (!transferEncoding.equals(IDENTITY_TRANSFER_ENCODING)) {
            throw new UnsupportedTransferEncodingException(transferEncoding);
        }

    }

}
