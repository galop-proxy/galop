package io.github.galop_proxy.galop.http;

import java.io.IOException;
import java.io.InputStream;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.Constants.HEADER_CHARSET;

final class LineReader {

    private static final int DEFAULT_SIZE = 8192;
    private static final int INCREASE_STEP = 8192;

    private final InputStream inputStream;

    private byte[] buffer;
    private int bufferIndex;

    LineReader(final InputStream inputStream) {
        this.inputStream = checkNotNull(inputStream, "inputStream");
        this.buffer = new byte[DEFAULT_SIZE];
    }

    int getBufferSize() {
        return buffer.length;
    }

    String readLine(final int maxSize) throws IOException {

        checkParameter(maxSize);
        resetBuffer();

        boolean carriageReturn = false;
        boolean lineBreak = false;

        while (!lineBreak) {

            final byte currentByte = readNextByte(maxSize);
            final char currentChar = (char) currentByte;

            if (currentChar == '\r') {
                carriageReturn = true;
            } else if (currentChar == '\n') {
                lineBreak = true;
            } else {
                carriageReturn = false;
            }

        }

        return bufferAsString(carriageReturn);

    }

    private void checkParameter(final int maxSize) {

        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be greater than 0.");
        }

    }

    private void resetBuffer() {
        bufferIndex = -1;
    }

    private byte readNextByte(final int maxSize) throws IOException {

        if (bufferIndex + 1 == maxSize) {
            throw new LineTooLargeException(maxSize);
        }

        final int nextByte = inputStream.read();

        if (nextByte < 0) {
            throw new IOException("Invalid end of line: No LB or CR LB.");
        }

        return addToBuffer(nextByte);

    }

    private byte addToBuffer(final int b) {

        bufferIndex++;

        if (bufferIndex == buffer.length) {
            growBuffer();
        }

        return buffer[bufferIndex] = (byte) b;

    }

    private void growBuffer() {
        final byte[] newBuffer = new byte[buffer.length + INCREASE_STEP];
        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
        buffer = newBuffer;
    }

    private String bufferAsString(final boolean carriageReturn) {

        final int bufferIndexWithoutCrLb;

        if (carriageReturn) {
            bufferIndexWithoutCrLb = bufferIndex - 2;
        } else {
            bufferIndexWithoutCrLb = bufferIndex - 1;
        }

        return new String(buffer, 0, bufferIndexWithoutCrLb + 1, HEADER_CHARSET);

    }

}
