package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.commons.ByteLimitExceededException;
import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.http.*;
import io.github.sebastianschmidt.galop.http.HttpHeaderParser.Result;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.sebastianschmidt.galop.http.HttpTestUtils.createGetRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ConnectionHandlerImpl}.
 */
public class ConnectionHandlerImplTest {

    private Configuration configuration;
    private HttpHeaderParser httpHeaderParser;
    private HttpMessageHandler httpMessageHandler;
    private Socket source;
    private Socket target;

    private ConnectionHandlerImpl connectionHandler;

    @Before
    public void setUp() throws IOException {

        configuration = mock(Configuration.class);
        when(configuration.getMaxHttpHeaderSize()).thenReturn(1024);

        httpHeaderParser = mock(HttpHeaderParser.class);

        httpMessageHandler = mock(HttpMessageHandler.class);
        doAnswer((invocation) -> {
            final InputStream inputStream = (InputStream) invocation.getArguments()[1];
            final OutputStream outputStream = (OutputStream) invocation.getArguments()[2];
            IOUtils.copyLarge(inputStream, outputStream);
            return null;
        }).when(httpMessageHandler).handle(any(), any(), any());

        source = mock(Socket.class);
        when(source.isClosed()).thenReturn(false);
        when(source.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        setInputContent("", source);

        target = mock(Socket.class);
        when(target.isClosed()).thenReturn(false);
        when(target.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        setInputContent("", target);

        connectionHandler = new ConnectionHandlerImpl(configuration, httpHeaderParser, httpMessageHandler, source,
                target);

    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new ConnectionHandlerImpl(null, httpHeaderParser, httpMessageHandler, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderParser_throwsNullPointerException() {
        new ConnectionHandlerImpl(configuration, null, httpMessageHandler, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpMessageHandler_throwsNullPointerException() {
        new ConnectionHandlerImpl(configuration, httpHeaderParser, null, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutSourceSocket_throwsNullPointerException() {
        new ConnectionHandlerImpl(configuration, httpHeaderParser, httpMessageHandler, null, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetSocket_throwsNullPointerException() {
        new ConnectionHandlerImpl(configuration, httpHeaderParser, httpMessageHandler, source, null);
    }

    // Handle one request and response:

    @Test
    public void run_withOneRequest_copiesRequestToRequestTarget() throws Exception {

        final String request = createGetRequest();
        setInputContent(request, source);
        when(source.isClosed()).thenReturn(false).thenReturn(true);

        connectionHandler.run();

        assertEquals(request, getOutputContent(target));

    }

    @Test
    public void run_withOneResponse_copiesResponseToRequestSource() throws Exception {

        final String responseContent = "<h1>Hello, world!</h1>";
        final String response = HttpTestUtils.createResponse(responseContent);
        setInputContent(response, target);
        when(source.isClosed()).thenReturn(false).thenReturn(true);

        connectionHandler.run();

        assertEquals(response, getOutputContent(source));

    }

    @Test
    public void run_whenHandleRequestAndResponseThrowsIOException_closesSocketsAndTerminates() throws Exception {

        doThrow(IOException.class).when(httpHeaderParser).parse(any(), anyInt());

        connectionHandler.run();

        verify(source).close();
        verify(target).close();

    }

    // Handle multiple requests and responses:

    @Test
    public void run_withMultipleRequests_copiesRequestsToRequestTarget() throws Exception {

        final String request1 = createGetRequest();
        final String request2 = createGetRequest();
        setInputContent(request1 + request2, source);
        when(source.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);

        connectionHandler.run();

        assertEquals(request1 + request2, getOutputContent(target));

    }

    @Test
    public void run_withMultipleResponses_copiesResponsesToRequestSource() throws Exception {

        final String responseContent = "<h1>Hello, world!</h1>";
        final String response1 = HttpTestUtils.createResponse(responseContent);
        final String response2 = HttpTestUtils.createResponse(responseContent);
        setInputContent(response1 + response2, target);
        when(source.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);

        connectionHandler.run();

        assertEquals(response1 + response2, getOutputContent(source));

    }

    // Closed sockets:

    @Test
    public void run_whenSourceSocketIsClosed_closesTargetSocketAndTerminates() throws Exception {
        when(source.isClosed()).thenReturn(true);
        connectionHandler.run();
        verify(target).close();
    }

    @Test
    public void run_whenTargetSocketIsClosed_closesSourceSocketAndTerminates() throws Exception {
        when(target.isClosed()).thenReturn(true);
        connectionHandler.run();
        verify(source).close();
    }

    // Interrupted execution:

    @Test
    public void run_whenThreadIsInterrupted_closesSourceAndTargetSocketAndTerminates() throws Exception {

        final int terminationTimeout = 30000;
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(connectionHandler);
        executorService.shutdownNow();

        verify(source, timeout(terminationTimeout)).close();
        verify(target, timeout(terminationTimeout)).close();

    }

    // Connection should be closed:

    @Test
    public void run_whenConnectionShouldBeClosed_closesSourceAndTargetSocketAndTerminatesAfterHandlingCurrentRequest()
            throws IOException {

        final String request = createGetRequest();
        setInputContent(request, source, () -> IOUtils.closeQuietly(connectionHandler));
        when(source.isClosed()).thenReturn(false);

        connectionHandler.run();

        assertEquals(request, getOutputContent(target));
        verify(source, atLeastOnce()).close();
        verify(target, atLeastOnce()).close();

    }

    // Configuration:

    @Test
    public void run_whenParsingHttpHeader_passesConfiguredMaxHttpHeaderSizeToParser() throws IOException {
        when(source.isClosed()).thenReturn(false).thenReturn(true);
        connectionHandler.run();
        verify(httpHeaderParser, atLeastOnce()).parse(any(), eq(1024));
        verify(httpHeaderParser, never()).parse(any(), not(eq(1024)));
    }

    // Invalid client request:

    @Test
    public void run_whenHttpHeaderParserThrowsInvalidHttpHeaderException_sendsStatusCode400ToClientAndClosesConnection()
            throws IOException {

        when(source.isClosed()).thenReturn(false);
        doThrow(InvalidHttpHeaderException.class).when(httpHeaderParser).parse(any(), anyInt(), any());

        connectionHandler.run();

        assertTrue(getOutputContent(source).startsWith("HTTP/1.1 400 Bad Request"));
        verify(source).close();
        verify(target).close();

    }

    @Test
    public void run_whenHttpHeaderParserThrowsByteLimitExceededException_sendsStatusCode431ToClientAndClosesConnection()
            throws IOException {

        when(source.isClosed()).thenReturn(false);
        doThrow(ByteLimitExceededException.class).when(httpHeaderParser).parse(any(), anyInt(), any());

        connectionHandler.run();

        assertTrue(getOutputContent(source).startsWith("HTTP/1.1 431 Request Header Fields Too Large"));
        verify(source).close();
        verify(target).close();

    }

    @Test
    public void run_whenHttpHeaderParserThrowsUnsupportedTransferEncodingException_sendsStatusCode411ToClientAndClosesConnection()
            throws IOException {

        when(source.isClosed()).thenReturn(false);
        doThrow(UnsupportedTransferEncodingException.class).when(httpHeaderParser).parse(any(), anyInt(), any());

        connectionHandler.run();

        assertTrue(getOutputContent(source).startsWith("HTTP/1.1 411 Length Required"));
        verify(source).close();
        verify(target).close();

    }

    // Invalid server response:

    @Test
    public void run_whenAnErrorOccursWhileParsingServerResponse_sendsBadeGatewayResponseToClientAndClosesConnections()
            throws IOException {

        when(source.isClosed()).thenReturn(false);
        doThrow(IOException.class).when(httpHeaderParser).parse(any(), anyInt());

        connectionHandler.run();

        assertTrue(getOutputContent(source).startsWith("HTTP/1.1 502 Bad Gateway"));
        verify(source).close();
        verify(target).close();

    }

    @Test
    public void run_whenANewErrorOccursDuringHandlingBadGatewayError_ignoresNewError() throws IOException {
        when(source.isClosed()).thenReturn(false).thenReturn(true);
        doThrow(IOException.class).when(httpHeaderParser).parse(any(), anyInt());
        doThrow(IOException.class).when(source).getOutputStream();
        connectionHandler.run();
    }

    // Helper methods:

    private void setInputContent(final String content, final Socket socket) throws IOException {
        setInputContent(content, socket, null);
    }

    private void setInputContent(final String content, final Socket socket, final Runnable callbackAfterCallback)
            throws IOException {

        final byte[] contentBytes = content.getBytes();

        final InputStream inputStream = new ByteArrayInputStream(contentBytes);
        when(socket.getInputStream()).thenReturn(inputStream);

        final long totalLength = (long) content.getBytes().length;
        final Result result = mock(Result.class);
        when(result.getTotalLength()).thenReturn(totalLength);
        when(httpHeaderParser.parse(any(), anyInt())).thenReturn(result);

        when(httpHeaderParser.parse(any(), anyInt(), any())).thenAnswer((invocation) -> {

            final Runnable callback = (Runnable) invocation.getArguments()[2];

            if (callback != null) {
                callback.run();
            }

            if (callbackAfterCallback != null) {
                callbackAfterCallback.run();
            }

            return result;

        });


    }

    private String getOutputContent(final Socket socket) throws IOException {
        return socket.getOutputStream().toString();
    }

}
