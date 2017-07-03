package io.github.galop_proxy.galop.http;

import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static io.github.galop_proxy.galop.http.Constants.HEADER_CHARSET;
import static io.github.galop_proxy.galop.http.Constants.NEW_LINE;
import static org.junit.Assert.assertArrayEquals;

/**
 * Tests the class {@link MessageBodyWriterImpl}.
 */
public class MessageBodyWriterImplTest {

    private MessageBodyWriter instance;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        instance = new MessageBodyWriterImpl();
        outputStream = new ByteArrayOutputStream();
    }

    // writeIdentityEntity:

    @Test
    public void writeIdentityEntity_withPositiveLength_writesGivenNumberOfBytesToOutputStream() throws IOException {
        assertIdentityEntity("Hello world!");
    }

    @Test
    public void writeIdentityEntity_withZeroLength_writesNoBytesToOutputStream() throws IOException {
        assertIdentityEntity("");
    }

    @Test(expected = NullPointerException.class)
    public void writeIdentityEntity_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.writeIdentityEntity(null, outputStream, 0);
    }

    @Test(expected = NullPointerException.class)
    public void writeIdentityEntity_withoutOutputStream_throwsNullPointerException() throws IOException {
        instance.writeIdentityEntity(new ByteArrayInputStream(new byte[0]), null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeIdentityEntity_withNegativeLength_throwsIllegalArgumentException() throws IOException {
        instance.writeIdentityEntity(new ByteArrayInputStream(new byte[0]), outputStream, -1);
    }

    // writeChunkedEntity:

    @Test
    public void writeChunkedEntity_withValidChunkedEntity_writesBytesToOutputStream() throws IOException {
        final String entity =
                  "6" + NEW_LINE + "Hello " + NEW_LINE
                + "11" + NEW_LINE + " wonderful world!" + NEW_LINE
                + "E" + NEW_LINE + "\nHow\r\nare you?" + NEW_LINE
                + "0" + NEW_LINE
                + NEW_LINE;
        assertChunkedEntity(entity);
    }

    @Test
    public void writeChunkedEntity_withChunkExtension_writesBytesToOutputStream() throws IOException {
        final String entity =
                  "C;lorem=ipsum" + NEW_LINE + "Hello world!" + NEW_LINE
                + "A;hello=world;foo=bar" + NEW_LINE + "123\r\n\r\n456" + NEW_LINE
                + "0" + NEW_LINE
                + NEW_LINE;
        assertChunkedEntity(entity);
    }

    @Test
    public void writeChunkedEntity_withTrailerPart_writesBytesToOutputStream() throws IOException {
        final String entity =
                  "B" + Constants.NEW_LINE + "Lorem Ipsum" + Constants.NEW_LINE
                + "0" + Constants.NEW_LINE
                + "Example:123456789" + Constants.NEW_LINE
                + "Another: Hello world!" + Constants.NEW_LINE
                + Constants.NEW_LINE;
        assertChunkedEntity(entity);
    }

    @Test(expected = InvalidChunkException.class)
    public void writeChunkedEntity_withoutValidEndOfChunkSize_throwsInvalidChunkException() throws IOException {
        final String entity = "1"; // Missing CRLF
        assertChunkedEntity(entity);
    }

    @Test(expected = InvalidChunkException.class)
    public void writeChunkedEntity_withInvalidChunkSize_throwsInvalidChunkException() throws IOException {
        final String entity = "Z" + Constants.NEW_LINE; // Not a valid Hexadecimal number
        assertChunkedEntity(entity);
    }

    @Test(expected = NullPointerException.class)
    public void writeChunkedEntity_withoutInputStream_throwsNullPointerException() throws IOException {
        instance.writeChunkedEntity(null, outputStream);
    }

    @Test(expected = NullPointerException.class)
    public void writeChunkedEntity_withoutOutputStream_throwsNullPointerException() throws IOException {
        instance.writeChunkedEntity(new ByteArrayInputStream(new byte[0]), null);
    }

    // Helper method:

    private void assertIdentityEntity(final String entityAsString) throws IOException {

        final String nextMessage = "Hello world!";

        final byte[] entity = entityAsString.getBytes(HEADER_CHARSET);
        final byte[] combined = (entityAsString + nextMessage).getBytes(HEADER_CHARSET);
        final InputStream inputStream = new ByteArrayInputStream(combined);

        instance.writeIdentityEntity(inputStream, outputStream, entity.length);

        assertArrayEquals(entity, outputStream.toByteArray());

    }

    private void assertChunkedEntity(final String entityAsString) throws IOException {

        final String nextMessage = "Hello world!";

        final byte[] entity = entityAsString.getBytes(HEADER_CHARSET);
        final byte[] combined = (entityAsString + nextMessage).getBytes(HEADER_CHARSET);
        final InputStream inputStream = new ByteArrayInputStream(combined);

        instance.writeChunkedEntity(inputStream, outputStream);

        assertArrayEquals(entity, outputStream.toByteArray());

    }

}
