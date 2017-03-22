package io.github.sebastianschmidt.galop.http;

import io.github.sebastianschmidt.galop.http.HttpHeaderParser.Result;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static io.github.sebastianschmidt.galop.http.HttpConstants.NEW_LINE;
import static io.github.sebastianschmidt.galop.http.HttpTestUtils.createResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link HttpMessageHandlerImpl}.
 */
public class HttpMessageHandlerImplTest {

    private HttpMessageHandlerImpl handler;
    private OutputStream outputStream;

    @Before
    public void setUp() {
        handler = new HttpMessageHandlerImpl();
        outputStream = new ByteArrayOutputStream();
    }

    // No entity:

    @Test
    public void handle_messageWithoutEntity_copiesMessageHeaderToOutput() throws IOException {

        final String message = "Hello world!";
        final int messageLength = message.getBytes().length;
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        final Result result = createHeaderResult(false, messageLength, (long) messageLength);

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    // Identity transfer encoding:

    @Test
    public void handle_messageWithEntityAndIdentityTransferEncoding_copiesMessageToOutput() throws IOException {

        final String header = "Hello world!";
        final String entity = "Lorem Ipsum";
        final String message = header + entity;
        final int headerLength = header.getBytes().length;
        final int messageLength = message.getBytes().length;
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        final Result result = createHeaderResult(false, headerLength, (long) messageLength);

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    // Chunked transfer encoding:

    @Test
    public void handle_messageWithChunkedTransferEncoding_copiesMessageToOutput() throws IOException {

        final String entity = "6" + NEW_LINE + "Hello " + NEW_LINE
                + "11" + NEW_LINE + " wonderful world!" + NEW_LINE
                + "E" + NEW_LINE + "\nHow\r\nare you?" + NEW_LINE
                + "0" + NEW_LINE
                + NEW_LINE;
        final String message = createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final Result result = createHeaderResult(true, headerLength, null);

        final String nextMessage = "Hello world again!";
        final String combinedMessages = message + nextMessage;
        final InputStream inputStream = new ByteArrayInputStream(combinedMessages.getBytes());

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    @Test
    public void handle_messageWithChunkedTransferEncodingAndChunkExtension_copiesMessageToOutput() throws IOException {

        final String entity = "C;lorem=ipsum" + NEW_LINE + "Hello world!" + NEW_LINE
                + "A;hello=world;foo=bar" + NEW_LINE + "123\r\n\r\n456" + NEW_LINE
                + "0" + NEW_LINE
                + NEW_LINE;
        final String message = createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final Result result = createHeaderResult(true, headerLength, null);

        final String nextMessage = "Hello world again!";
        final String combinedMessages = message + nextMessage;
        final InputStream inputStream = new ByteArrayInputStream(combinedMessages.getBytes());

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    @Test
    public void handle_messageWithChunkedTransferEncodingAndTrailerPart_copiesMessageToOutput() throws IOException {

        final String entity = "B" + NEW_LINE + "Lorem Ipsum" + NEW_LINE
                + "0" + NEW_LINE
                + "Example:123456789" + NEW_LINE
                + "Another: Hello world!" + NEW_LINE
                + NEW_LINE;
        final String message = createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final Result result = createHeaderResult(true, headerLength, null);

        final String nextMessage = "Hello world again!";
        final String combinedMessages = message + nextMessage;
        final InputStream inputStream = new ByteArrayInputStream(combinedMessages.getBytes());

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    // Invalid chunked transfer encoding:

    @Test(expected = InvalidChunkException.class)
    public void handle_withoutValidEndOfChunkSize_throwsInvalidChunkException() throws IOException {
        final String entity = "1"; // Missing CRLF
        final String message = createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final Result result = createHeaderResult(true, headerLength, null);
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        handler.handle(result, inputStream, outputStream);
    }

    @Test(expected = InvalidChunkException.class)
    public void handle_withInvalidChunkSize_throwsInvalidChunkException() throws IOException {
        final String entity = "Z" + NEW_LINE; // Not a valid Hexadecimal number
        final String message = createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final Result result = createHeaderResult(true, headerLength, null);
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        handler.handle(result, inputStream, outputStream);
    }

    // Invalid parameters:

    @Test(expected = NullPointerException.class)
    public void handle_withoutHttpHeaderParserResult_throwsNullPointerException() throws IOException {
        handler.handle(null, mock(InputStream.class), mock(OutputStream.class));
    }

    @Test(expected = NullPointerException.class)
    public void handle_withoutInputStream_throwsNullPointerException() throws IOException {
        handler.handle(mock(Result.class), null, mock(OutputStream.class));
    }

    @Test(expected = NullPointerException.class)
    public void handle_withoutOutputStream_throwsNullPointerException() throws IOException {
        handler.handle(mock(Result.class), mock(InputStream.class), null);
    }

    // Helper methods:

    private Result createHeaderResult(final boolean chunkedTransferEncoding, final long headerLength,
                                      final Long totalLength) {
        final Result result = mock(Result.class);
        when(result.isChunkedTransferEncoding()).thenReturn(chunkedTransferEncoding);
        when(result.getHeaderLength()).thenReturn(headerLength);
        when(result.getTotalLength()).thenReturn(totalLength);
        return result;
    }

}
