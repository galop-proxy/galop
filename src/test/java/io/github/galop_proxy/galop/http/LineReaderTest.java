package io.github.galop_proxy.galop.http;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the class {@link LineReader}.
 */
public class LineReaderTest {

    private static final int DEFAULT_SIZE = createLineReader().getBufferSize();
    private static final int MAX_SIZE = DEFAULT_SIZE * 2;

    // Valid lines:

    @Test
    public void readLine_withLineWithCrLb_returnsLineWithoutCrLb() throws IOException {
        assertLine("Hello world!", "Hello world!\r\n");
    }

    @Test
    public void readLine_withLineWithLb_returnsLineWithoutLb() throws IOException {
        assertLine("Hello world!", "Hello world!\n");
    }

    @Test
    public void readLine_withEmptyLine_returnsEmptyString() throws IOException {
        assertLine("", "\r\n");
    }

    @Test
    public void readLine_multipleTimes_returnsMultipleLines() throws IOException {
        final String[] messages = new String[] { "Hello world!", "Lorem ipsum!" };
        final String[] lines = new String[] { "Hello world!\r\n", "Lorem ipsum!\r\n" };
        assertLines(messages, lines);
    }

    @Test
    public void readLine_withLongerLineThanCurrentBuffer_growsBufferAndReturnsLineWithoutCrLb() throws IOException {

        final String message = StringUtils.repeat('A', DEFAULT_SIZE);
        final String line = message + "\r\n";

        final LineReader lineReader = createLineReader(line);
        final int initialBufferSize = lineReader.getBufferSize();

        assertEquals(message, lineReader.readLine(MAX_SIZE));
        assertTrue(lineReader.getBufferSize() > initialBufferSize);

    }

    // Invalid lines:

    @Test(expected = LineTooLargeException.class)
    public void readLine_withLineLongerThanMaxSize_throwsLineTooLargeException() throws IOException {
        final String message = StringUtils.repeat('A', MAX_SIZE);
        final String line = message + "\r\n";
        createLineReader(line).readLine(MAX_SIZE);
    }

    @Test(expected = IOException.class)
    public void readLine_withoutLineBreak_throwsIOException() throws IOException {
        createLineReader("Hello world").readLine(MAX_SIZE);
    }

    // Wrong use of API:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutInputStream_throwsNullPointerException() {
        new LineReader(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readLine_withNegativeMaxSize_throwsIllegalArgumentException() throws IOException {
        createLineReader().readLine(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readLine_withZeroMaxSize_throwsIllegalArgumentException() throws IOException {
        createLineReader().readLine(0);
    }

    // Helper methods:

    private static void assertLines(final String[] expectedLines, final String[] lines) throws IOException {

        final String input = Arrays.stream(lines).collect(Collectors.joining(""));
        final LineReader lineReader = createLineReader(input);

        for (final String expectedLine : expectedLines) {
            assertEquals(expectedLine, lineReader.readLine(MAX_SIZE));
        }

    }

    private static void assertLine(final String expectedLine, final String input) throws IOException {
        assertLines(new String[] { expectedLine }, new String[] { input });
    }

    private static LineReader createLineReader(final String content) {

        final byte[] bytes;

        if (content != null) {
            bytes = content.getBytes();
        } else {
            bytes = new byte[0];
        }

        final InputStream inputStream = new ByteArrayInputStream(bytes);
        return new LineReader(inputStream);

    }

    private static LineReader createLineReader() {
        return createLineReader(null);
    }

}
