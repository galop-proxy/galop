package io.github.sebastianschmidt.galop;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public class LimitedInputStream extends InputStream {

    private final InputStream original;
    private final long bytesLimit;
    private long totalNumberOfBytesRead;

    public LimitedInputStream(final InputStream original, final long bytesLimit) {

        this.original = requireNonNull(original, "original must not be null.");

        if (bytesLimit < 0) {
            throw new IllegalArgumentException("bytesLimit must be at least 0.");
        }

        this.bytesLimit = bytesLimit;

    }

    @Override
    public int read() throws IOException {

        final int nextByte = original.read();

        if (nextByte > -1) {
            incrementTotalNumberOfBytesRead(1);
        }

        return nextByte;

    }

    @Override
    public int read(final byte[] bytes) throws IOException {
        return read(bytes, 0, bytes.length);
    }

    @Override
    public int read(final byte[] bytes, final int offset, final int length) throws IOException {

        final int numberOfBytesRead = original.read(bytes, offset, length);

        if (numberOfBytesRead > -1) {
            incrementTotalNumberOfBytesRead(numberOfBytesRead);
        }

        return numberOfBytesRead;

    }

    private void incrementTotalNumberOfBytesRead(final int numberOfBytes) throws IOException {

        totalNumberOfBytesRead += numberOfBytes;

        if (totalNumberOfBytesRead > bytesLimit) {
            throw new IOException("The limit of read bytes was exceeded.");
        }

    }

    @Override
    public int available() throws IOException {
        return original.available();
    }

    @Override
    public void close() throws IOException {
        original.close();
    }

}
