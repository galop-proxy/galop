package io.github.sebastianschmidt.galop;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.github.sebastianschmidt.galop.HttpTestUtils.createHttpRequest;
import static io.github.sebastianschmidt.galop.HttpTestUtils.createHttpResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ConnectionHandler}.
 */
public class ConnectionHandlerTest {

    private static final long TERMINATION_TIMEOUT = 30000;

    private ExecutorService executorService;
    private HttpHeaderParser httpHeaderParser;

    private Socket source;
    private Socket target;

    private ConnectionHandler connectionHandler;

    @Before
    public void setUp() throws IOException {

        executorService = Executors.newCachedThreadPool();
        httpHeaderParser = new HttpHeaderParser(255);

        source = mock(Socket.class);
        when(source.isClosed()).thenReturn(false);
        when(source.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        setInputContent("", source);

        target = mock(Socket.class);
        when(target.isClosed()).thenReturn(false);
        when(target.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        setInputContent("", target);

        connectionHandler = new ConnectionHandler(httpHeaderParser, source, target);

    }

    // constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderParser_throwsNullPointerException() {
        new ConnectionHandler(null, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutSourceSocket_throwsNullPointerException() {
        new ConnectionHandler(httpHeaderParser, null, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetSocket_throwsNullPointerException() {
        new ConnectionHandler(httpHeaderParser, source, null);
    }

    // Handle one request and response:

    @Test
    public void run_withOneRequest_copiesRequestToRequestTarget() throws Exception {

        final String request = createHttpRequest();
        setInputContent(request, source);

        when(source.isClosed()).thenReturn(false).thenReturn(true);
        executorService.execute(connectionHandler);
        awaitTermination();

        assertEquals(request, getOutputContent(target));

    }

    @Test
    public void run_withOneResponse_copiesResponseToRequestSource() throws Exception {

        final String responseContent = "<h1>Hello, world!</h1>";
        final String response = createHttpResponse(responseContent);
        setInputContent(response, target);

        when(source.isClosed()).thenReturn(false).thenReturn(true);
        executorService.execute(connectionHandler);
        awaitTermination();

        assertEquals(response, getOutputContent(source));

    }

    @Test
    public void run_whenHandleRequestAndResponseThrowsIOException_closesSocketsAndTerminates() throws Exception {

        httpHeaderParser = mock(HttpHeaderParser.class);
        connectionHandler = new ConnectionHandler(httpHeaderParser, source, target);
        doThrow(IOException.class).when(httpHeaderParser).calculateRequestLength(any());

        executorService.execute(connectionHandler);

        verify(source, timeout(TERMINATION_TIMEOUT)).close();
        verify(target, timeout(TERMINATION_TIMEOUT)).close();

    }

    // Handle multiple requests and responses:

    @Test
    public void run_withMultipleRequests_copiesRequestsToRequestTarget() throws Exception {

        final String request1 = createHttpRequest();
        final String request2 = createHttpRequest();
        setInputContent(request1 + request2, source);

        when(source.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);
        executorService.execute(connectionHandler);
        awaitTermination();

        assertEquals(request1 + request2, getOutputContent(target));

    }

    @Test
    public void run_withMultipleResponses_copiesResponsesToRequestSource() throws Exception {

        final String responseContent = "<h1>Hello, world!</h1>";
        final String response1 = createHttpResponse(responseContent);
        final String response2 = createHttpResponse(responseContent);
        setInputContent(response1 + response2, target);

        when(source.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);
        executorService.execute(connectionHandler);
        awaitTermination();

        assertEquals(response1 + response2, getOutputContent(source));

    }

    // Closed sockets:

    @Test
    public void run_whenSourceSocketIsClosed_closesTargetSocketAndTerminates() throws Exception {
        when(source.isClosed()).thenReturn(true);
        executorService.execute(connectionHandler);
        verify(target, timeout(TERMINATION_TIMEOUT)).close();
    }

    @Test
    public void run_whenTargetSocketIsClosed_closesSourceSocketAndTerminates() throws Exception {
        when(target.isClosed()).thenReturn(true);
        executorService.execute(connectionHandler);
        verify(source, timeout(TERMINATION_TIMEOUT)).close();
    }

    // Interrupted execution:

    @Test
    public void run_whenThreadIsInterrupted_closesSourceAndTargetSocketAndTerminates() throws Exception {
        executorService.execute(connectionHandler);
        executorService.shutdownNow();
        verify(source, timeout(TERMINATION_TIMEOUT)).close();
        verify(target, timeout(TERMINATION_TIMEOUT)).close();
    }

    // Helper methods:

    private void setInputContent(final String content, final Socket socket) throws IOException {
        final byte[] contentBytes = content.getBytes();
        final InputStream inputStream = new ByteArrayInputStream(contentBytes);
        when(socket.getInputStream()).thenReturn(inputStream);
    }

    private String getOutputContent(final Socket socket) throws IOException {
        return socket.getOutputStream().toString();
    }

    private void awaitTermination() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
    }

}
