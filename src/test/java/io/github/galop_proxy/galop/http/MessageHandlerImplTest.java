package io.github.galop_proxy.galop.http;

import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link MessageHandlerImpl}.
 */
public class MessageHandlerImplTest {

    private MessageHandlerImpl handler;
    private OutputStream outputStream;

    @Before
    public void setUp() {
        handler = new MessageHandlerImpl();
        outputStream = new ByteArrayOutputStream();
    }

    // No entity:

    @Test
    public void handle_messageWithoutEntity_copiesMessageHeaderToOutput() throws IOException {

        final String message = "Hello world!";
        final int messageLength = message.getBytes().length;
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        final HttpHeaderParser.Result result = createHeaderResult(false, messageLength, (long) messageLength);

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
        final HttpHeaderParser.Result result = createHeaderResult(false, headerLength, (long) messageLength);

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    // Chunked transfer encoding:

    @Test
    public void handle_messageWithChunkedTransferEncoding_copiesMessageToOutput() throws IOException {

        final String entity = "6" + Constants.NEW_LINE + "Hello " + Constants.NEW_LINE
                + "11" + Constants.NEW_LINE + " wonderful world!" + Constants.NEW_LINE
                + "E" + Constants.NEW_LINE + "\nHow\r\nare you?" + Constants.NEW_LINE
                + "0" + Constants.NEW_LINE
                + Constants.NEW_LINE;
        final String message = HttpTestUtils.createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final HttpHeaderParser.Result result = createHeaderResult(true, headerLength, null);

        final String nextMessage = "Hello world again!";
        final String combinedMessages = message + nextMessage;
        final InputStream inputStream = new ByteArrayInputStream(combinedMessages.getBytes());

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    @Test
    public void handle_messageWithChunkedTransferEncodingAndChunkExtension_copiesMessageToOutput() throws IOException {

        final String entity = "C;lorem=ipsum" + Constants.NEW_LINE + "Hello world!" + Constants.NEW_LINE
                + "A;hello=world;foo=bar" + Constants.NEW_LINE + "123\r\n\r\n456" + Constants.NEW_LINE
                + "0" + Constants.NEW_LINE
                + Constants.NEW_LINE;
        final String message = HttpTestUtils.createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final HttpHeaderParser.Result result = createHeaderResult(true, headerLength, null);

        final String nextMessage = "Hello world again!";
        final String combinedMessages = message + nextMessage;
        final InputStream inputStream = new ByteArrayInputStream(combinedMessages.getBytes());

        handler.handle(result, inputStream, outputStream);

        assertEquals(message, outputStream.toString());

    }

    @Test
    public void handle_messageWithChunkedTransferEncodingAndTrailerPart_copiesMessageToOutput() throws IOException {

        final String entity = "B" + Constants.NEW_LINE + "Lorem Ipsum" + Constants.NEW_LINE
                + "0" + Constants.NEW_LINE
                + "Example:123456789" + Constants.NEW_LINE
                + "Another: Hello world!" + Constants.NEW_LINE
                + Constants.NEW_LINE;
        final String message = HttpTestUtils.createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final HttpHeaderParser.Result result = createHeaderResult(true, headerLength, null);

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
        final String message = HttpTestUtils.createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final HttpHeaderParser.Result result = createHeaderResult(true, headerLength, null);
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        handler.handle(result, inputStream, outputStream);
    }

    @Test(expected = InvalidChunkException.class)
    public void handle_withInvalidChunkSize_throwsInvalidChunkException() throws IOException {
        final String entity = "Z" + Constants.NEW_LINE; // Not a valid Hexadecimal number
        final String message = HttpTestUtils.createResponse(entity, null, "chunked");
        final long headerLength = message.getBytes().length - entity.getBytes().length;
        final HttpHeaderParser.Result result = createHeaderResult(true, headerLength, null);
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
        handler.handle(mock(HttpHeaderParser.Result.class), null, mock(OutputStream.class));
    }

    @Test(expected = NullPointerException.class)
    public void handle_withoutOutputStream_throwsNullPointerException() throws IOException {
        handler.handle(mock(HttpHeaderParser.Result.class), mock(InputStream.class), null);
    }

    // Helper methods:

    private HttpHeaderParser.Result createHeaderResult(final boolean chunkedTransferEncoding, final long headerLength,
                                                       final Long totalLength) {
        final HttpHeaderParser.Result result = mock(HttpHeaderParser.Result.class);
        when(result.isChunkedTransferEncoding()).thenReturn(chunkedTransferEncoding);
        when(result.getHeaderLength()).thenReturn(headerLength);
        when(result.getTotalLength()).thenReturn(totalLength);
        return result;
    }

}
