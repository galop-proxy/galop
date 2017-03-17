package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.parser.HttpTestUtils;
import io.github.sebastianschmidt.galop.parser.HttpHeaderParser;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.sebastianschmidt.galop.parser.HttpTestUtils.createGetRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ConnectionHandler}.
 */
public class ConnectionHandlerTest {

    private Configuration configuration;
    private HttpHeaderParser httpHeaderParser;
    private Socket source;
    private Socket target;

    private ConnectionHandler connectionHandler;

    @Before
    public void setUp() throws IOException {

        configuration = mock(Configuration.class);
        when(configuration.getMaxHttpHeaderSize()).thenReturn(1024);

        httpHeaderParser = mock(HttpHeaderParser.class);

        source = mock(Socket.class);
        when(source.isClosed()).thenReturn(false);
        when(source.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        setInputContent("", source);

        target = mock(Socket.class);
        when(target.isClosed()).thenReturn(false);
        when(target.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        setInputContent("", target);

        connectionHandler = new ConnectionHandler(configuration, httpHeaderParser, source, target);

    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new ConnectionHandler(null, httpHeaderParser, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderParser_throwsNullPointerException() {
        new ConnectionHandler(configuration, null, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutSourceSocket_throwsNullPointerException() {
        new ConnectionHandler(configuration, httpHeaderParser, null, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetSocket_throwsNullPointerException() {
        new ConnectionHandler(configuration, httpHeaderParser, source, null);
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

        doThrow(IOException.class).when(httpHeaderParser).calculateTotalLength(any(), anyInt());

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

    // Configuration:

    @Test
    public void run_whenParsingHttpHeader_passesConfiguredMaxHttpHeaderSizeToParser() throws IOException {
        when(source.isClosed()).thenReturn(false).thenReturn(true);
        connectionHandler.run();
        verify(httpHeaderParser, atLeastOnce()).calculateTotalLength(any(), eq(1024));
        verify(httpHeaderParser, never()).calculateTotalLength(any(), not(eq(1024)));
    }

    // Helper methods:

    private void setInputContent(final String content, final Socket socket) throws IOException {
        final byte[] contentBytes = content.getBytes();
        final InputStream inputStream = new ByteArrayInputStream(contentBytes);
        when(socket.getInputStream()).thenReturn(inputStream);
        when(httpHeaderParser.calculateTotalLength(any(), anyInt())).thenReturn((long) content.getBytes().length);
    }

    private String getOutputContent(final Socket socket) throws IOException {
        return socket.getOutputStream().toString();
    }

}