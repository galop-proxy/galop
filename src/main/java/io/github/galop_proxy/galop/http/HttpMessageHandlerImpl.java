package io.github.galop_proxy.galop.http;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;

final class HttpMessageHandlerImpl implements HttpMessageHandler {

    @Override
    public void handle(final HttpHeaderParser.Result header, final InputStream inputStream,
                       final OutputStream outputStream) throws IOException {

        checkNotNull(header, "header");
        checkNotNull(inputStream, "inputStream");
        checkNotNull(outputStream, "outputStream");

        if (header.isChunkedTransferEncoding()) {
            handleChunkedTransferEncoding(header, inputStream, outputStream);
        } else {
            handleIdentityTransferEncoding(header, inputStream, outputStream);
        }

    }

    private void handleIdentityTransferEncoding(final HttpHeaderParser.Result header, final InputStream inputStream,
                                                final OutputStream outputStream) throws IOException {
        IOUtils.copyLarge(inputStream, outputStream, 0, header.getTotalLength());
    }

    private void handleChunkedTransferEncoding(final HttpHeaderParser.Result header, final InputStream inputStream,
                                               final OutputStream outputStream) throws IOException {
        IOUtils.copyLarge(inputStream, outputStream, 0, header.getHeaderLength());
        handleChunks(inputStream, outputStream);
        handleTrailerPart(inputStream, outputStream);
    }

    private void handleChunks(final InputStream inputStream, final OutputStream outputStream) throws IOException {

        boolean lastChunk = false;

        while (!lastChunk) {
            lastChunk = handleChunk(inputStream, outputStream);
        }

    }

    private boolean handleChunk(final InputStream inputStream, final OutputStream outputStream) throws IOException {

        long chunkSize = readChunkSize(inputStream, outputStream);
        boolean lastChunk = chunkSize == 0;

        if (!lastChunk) {
            // Copy chunk data and the following CRLF.
            IOUtils.copyLarge(inputStream, outputStream, 0, chunkSize + 2);
        }

        return lastChunk;

    }

    private long readChunkSize(final InputStream inputStream, final OutputStream outputStream) throws IOException {

        final StringBuilder sizeBuilder = new StringBuilder("0x");
        boolean endChunkSize = false;
        boolean chunkExtension = false;
        int currentByte;

        while (!endChunkSize && (currentByte = inputStream.read()) > -1) {

            char currentChar = (char) currentByte;

            if (currentChar == '\n' || currentChar == ';') {

                endChunkSize = true;

                if (currentChar == ';') {
                    chunkExtension = true;
                }

            } else if (currentChar != '\r') {
                sizeBuilder.append(currentChar);
            }

            outputStream.write(currentByte);

        }

        if (!endChunkSize) {
            throw new InvalidChunkException("Invalid chunk: No valid end of chunk size.");
        }

        if (chunkExtension) {
            skipChunkExtensions(inputStream, outputStream);
        }

        try {
            return Long.decode(sizeBuilder.toString());
        } catch (final NumberFormatException ex) {
            throw new InvalidChunkException("Invalid chunk: Invalid chunk size.", ex);
        }

    }

    private void skipChunkExtensions(final InputStream inputStream, final OutputStream outputStream) throws IOException {

        boolean endChunkExtensions = false;
        int currentByte;

        while (!endChunkExtensions && (currentByte = inputStream.read()) > -1) {

            char currentChar = (char) currentByte;

            if (currentChar == '\n') {
                endChunkExtensions = true;
            }

            outputStream.write(currentByte);

        }

    }

    private void handleTrailerPart(final InputStream inputStream, final OutputStream outputStream) throws IOException {

        char[] lastCharacters = new char[] { '\r', '\n', '\u0000', '\u0000' };
        int currentIndex = 2;

        boolean endTrailerPart = false;
        int currentByte;

        while (!endTrailerPart && (currentByte = inputStream.read()) > -1) {

            char currentChar = (char) currentByte;
            lastCharacters[currentIndex] = currentChar;

            if (lastCharacters[getPreviousIndex(currentIndex, 3)] == '\r' &&
                    lastCharacters[getPreviousIndex(currentIndex, 2)] == '\n' &&
                    lastCharacters[getPreviousIndex(currentIndex, 1)] == '\r' &&
                    lastCharacters[currentIndex] == '\n') {
                endTrailerPart = true;
            }

            if (currentIndex == 3) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }

            outputStream.write(currentByte);

        }

    }

    private int getPreviousIndex(final int currentIndex, final int previous) {

        int previousIndex = currentIndex - previous;

        if (previousIndex >= 0) {
            return currentIndex - previous;
        } else {
            return 4 + previousIndex;
        }

    }

}
