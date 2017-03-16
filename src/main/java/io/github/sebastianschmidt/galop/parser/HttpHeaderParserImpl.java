package io.github.sebastianschmidt.galop.parser;

import io.github.sebastianschmidt.galop.commons.LimitedInputStream;

import java.io.*;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

final class HttpHeaderParserImpl implements HttpHeaderParser {

    private static final Charset HTTP_HEADER_CHARSET = Charset.forName("ASCII");
    private static final String HEADER_CONTENT_LENGTH_PREFIX = "Content-Length:";
    private static final String HEADER_TRANSFER_ENCODING_PREFIX = "Transfer-Encoding:";
    private static final String IDENTITY_TRANSFER_ENCODING = "identity";

    @Override
    public long calculateTotalLength(final InputStream inputStream, final int maxHttpHeaderSize) throws IOException {

        requireNonNull(inputStream, "inputStream must not be null.");

        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark.");
        }

        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, maxHttpHeaderSize);
        inputStream.mark(maxHttpHeaderSize);

        final long calculatedLength = parseRequest(limitedInputStream, maxHttpHeaderSize);
        inputStream.reset();
        return calculatedLength;

    }

    private long parseRequest(final LimitedInputStream limitedInputStream, final int maxHttpHeaderSize) throws IOException {

        byte[] bytes = new byte[maxHttpHeaderSize];
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
