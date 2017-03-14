package io.github.sebastianschmidt.galop;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link LimitedInputStream}.
 */
public class LimitedInputStreamTest {

    private String exampleString;
    private byte[] exampleBytes;
    private LimitedInputStream limitedInputStream;
    private LimitedInputStream limitedInputStreamLimitToLow;

    @Before
    public void setUp() {

        exampleString = "Hello, world!";
        exampleBytes = exampleString.getBytes(Charset.defaultCharset());

        limitedInputStream = new LimitedInputStream(
                IOUtils.toInputStream(exampleString, Charset.defaultCharset()), exampleBytes.length);
        limitedInputStreamLimitToLow = new LimitedInputStream(
                IOUtils.toInputStream(exampleString, Charset.defaultCharset()), exampleBytes.length - 1);

    }

    @Test(expected = NullPointerException.class)
    public void construct_withoutInputStream_throwsNullPointerException() {
        new LimitedInputStream(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_withBytesLimitLessThanZero_throwsIllegalArgumentException() {
        final InputStream inputStream = IOUtils.toInputStream("Hello, world!", Charset.defaultCharset());
        new LimitedInputStream(inputStream, -1);
    }

    @Test
    public void read_whenBytesLimitWasNotExceeded_returnsNextByte() throws IOException {
        assertEquals(exampleBytes[0], limitedInputStream.read());
        assertEquals(exampleBytes[1], limitedInputStream.read());
    }

    @Test(expected = IOException.class)
    public void read_whenBytesLimitWasExceeded_throwsIOException() throws IOException {
        while (limitedInputStreamLimitToLow.read() != -1);
    }

    @Test
    public void readMultipleBytes_whenBytesLimitWasNotExceeded_writesNextBytesToArray() throws IOException {
        final byte[] bytes = new byte[2];
        limitedInputStream.read(bytes);
        assertEquals(exampleBytes[0], bytes[0]);
        assertEquals(exampleBytes[1], bytes[1]);
    }

    @Test(expected = IOException.class)
    public void readMultipleBytes_whenBytesLimitWasExceeded_throwsIOException() throws IOException {
        while (limitedInputStreamLimitToLow.read(new byte[2]) != -1);
    }

    @Test
    public void available_callsAvailableFromOriginalInputStream() throws IOException {

        final InputStream inputStream = mock(InputStream.class);
        when(inputStream.available()).thenReturn(123);

        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, 1);
        assertEquals(123, limitedInputStream.available());
        verify(inputStream).available();

    }

    @Test
    public void close_closesOriginalInputStream() throws IOException {
        final InputStream inputStream = mock(InputStream.class);
        final LimitedInputStream limitedInputStream = new LimitedInputStream(inputStream, 1);
        limitedInputStream.close();
        verify(inputStream).close();
    }

}
