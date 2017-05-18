package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.galop.commons.ByteLimitExceededException;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.http.HttpHeaderParser.Result;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link HttpExchangeHandlerImpl}.
 */
public class HttpExchangeHandlerImplTest {

    private final static long REQUEST_TIMEOUT = 10000;
    private final static long RESPONSE_TIMEOUT = 20000;
    private final static int MAX_HEADER_SIZE = 1024;

    private Result headerResult;
    private HttpHeaderParser httpHeaderParser;
    private HttpMessageHandler httpMessageHandler;
    private Future<Result> future;
    private ExecutorService executorService;
    private HttpExchangeHandlerImpl handler;

    private Socket source;
    private OutputStream sourceOutputStream;
    private Socket target;
    private Configuration configuration;
    private Runnable callback;

    @Before
    public void setUp() throws Exception {

        httpHeaderParser = mockHttpHeaderParser();
        httpMessageHandler = mock(HttpMessageHandler.class);
        executorService = mockExecutorService();
        handler = new HttpExchangeHandlerImpl(httpHeaderParser, httpMessageHandler, executorService);

        source = mockSocket();
        sourceOutputStream = new ByteArrayOutputStream();
        when(source.getOutputStream()).thenReturn(sourceOutputStream);
        target = mockSocket();
        configuration = mockConfiguration();
        callback = spy(Runnable.class);

    }

    private HttpHeaderParser mockHttpHeaderParser() throws IOException {
        final HttpHeaderParser httpHeaderParser = mock(HttpHeaderParser.class);
        headerResult = mock(Result.class);
        when(httpHeaderParser.parse(any(), anyInt(), any())).thenReturn(headerResult);
        when(httpHeaderParser.parse(any(), anyInt())).thenReturn(headerResult);
        return httpHeaderParser;
    }

    @SuppressWarnings("unchecked")
    private ExecutorService mockExecutorService() throws Exception {

        future = mock(Future.class);
        when(future.get(anyLong(), any())).thenReturn(headerResult);

        final ExecutorService executorService = mock(ExecutorService.class);
        doAnswer(invocation -> {
            final Callable<?> callable = (Callable<?>) invocation.getArguments()[0];
            callable.call();
            return future;
        }).when(executorService).submit((Callable<?>) any());

        return executorService;

    }

    private Socket mockSocket() throws IOException {
        final Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        return socket;
    }

    private Configuration mockConfiguration() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getHttpHeaderRequestReceiveTimeout()).thenReturn(REQUEST_TIMEOUT);
        when(configuration.getHttpHeaderResponseReceiveTimeout()).thenReturn(RESPONSE_TIMEOUT);
        when(configuration.getMaxHttpHeaderSize()).thenReturn(MAX_HEADER_SIZE);
        return configuration;
    }

    // Handle request:

    @Test
    public void handleRequest_withoutClientOrServerErrors_callsParserAndHandler() throws Exception {
        handler.handleRequest(source, target, configuration, callback);
        verify(httpHeaderParser).parse(any(), eq(MAX_HEADER_SIZE), same(callback));
        verify(httpMessageHandler).handle(same(headerResult), any(), any());
        verify(future).get(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void handleRequest_withUnsupportedTransferEncoding_sendsStatusCode411ToClient() throws Exception {

        doThrow(UnsupportedTransferEncodingException.class).when(httpHeaderParser).parse(any(), anyInt(), any());

        try {
            handler.handleRequest(source, target, configuration, callback);
        } catch (final UnsupportedTransferEncodingException ex) {
            assertHttpStatusCode(HttpStatusCode.LENGTH_REQUIRED);
            throw ex;
        }

    }

    @Test(expected = ByteLimitExceededException.class)
    public void handleRequest_withTooLongRequestHeader_sendsStatusCode431ToClient() throws Exception {

        doThrow(ByteLimitExceededException.class).when(httpHeaderParser).parse(any(), anyInt(), any());

        try {
            handler.handleRequest(source, target, configuration, callback);
        } catch (final ByteLimitExceededException ex) {
            assertHttpStatusCode(HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE);
            throw ex;
        }

    }

    @Test
    public void handleRequest_withInvalidRequestHeader_sendsStatusCode400ToClient() throws Exception {

        doThrow(Exception.class).when(httpHeaderParser).parse(any(), anyInt(), any());

        try {
            handler.handleRequest(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.BAD_REQUEST);
        }

    }

    @Test
    public void handleRequest_whenInterrupted_sendsStatusCode503ToClient() throws Exception {

        doThrow(InterruptedException.class).when(future).get(anyLong(), any());

        try {
            handler.handleRequest(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.SERVICE_UNAVAILABLE);
        }

    }

    @Test
    public void handleRequest_whenReceiveRequestHeaderTimeout_sendsStatusCode408ToClient() throws Exception {

        doThrow(TimeoutException.class).when(future).get(anyLong(), any());

        try {
            handler.handleRequest(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.REQUEST_TIMEOUT);
        }

    }

    @Test
    public void handleRequest_whenAnErrorOccurredWhileSendingToServer_sendsStatusCode400ToClient() throws Exception {

        doThrow(Exception.class).when(httpMessageHandler).handle(any(), any(), any());

        try {
            handler.handleRequest(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.BAD_REQUEST);
        }

    }

    @Test
    public void handleRequest_whenAnUnexpectedErrorOccurredWhileParsingHeader_treatErrorAsInvalidRequestHeaderError()
            throws  Exception{

        doThrow(new ExecutionException(new NullPointerException())).when(future).get(anyLong(), any());

        try {
            handler.handleRequest(source, target, configuration, callback);
            fail("IOException expected.");
        } catch (final IOException ex) {
            assertHttpStatusCode(HttpStatusCode.BAD_REQUEST);
        }

    }

    @Test(expected = IOException.class)
    public void handleRequest_whenAnErrorOccurredWhileSendingStatusCodeToClient_ignoresNewError()
            throws Exception {
        doThrow(new ExecutionException(new IOException())).when(future).get(anyLong(), any());
        when(source.getOutputStream()).thenReturn(null);
        handler.handleRequest(source, target, configuration, callback);
    }

    // Handle response:

    @Test
    public void handleResponse_withoutClientOrServerErrors_callsParserAndHandler() throws Exception {
        handler.handleResponse(source, target, configuration, callback);
        verify(httpHeaderParser).parse(any(), eq(MAX_HEADER_SIZE));
        verify(httpMessageHandler).handle(same(headerResult), any(), any());
        verify(callback).run();
        verify(future).get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Test
    public void handleResponse_withInvalidResponseHeader_sendsStatusCode502ToClient() throws Exception {

        doThrow(Exception.class).when(httpHeaderParser).parse(any(), anyInt());

        try {
            handler.handleResponse(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.BAD_GATEWAY);
        }

    }

    @Test
    public void handleResponse_whenInterrupted_sendsStatusCode503ToClient() throws Exception {

        doThrow(InterruptedException.class).when(future).get(anyLong(), any());

        try {
            handler.handleResponse(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.SERVICE_UNAVAILABLE);
        }

    }

    @Test
    public void handleResponse_whenReceiveResponseHeaderTimeout_sendsStatusCode504ToClient() throws Exception {

        doThrow(TimeoutException.class).when(future).get(anyLong(), any());

        try {
            handler.handleResponse(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.GATEWAY_TIMEOUT);
        }

    }

    @Test
    public void handleResponse_whenAnErrorOccurredWhileSendingToClient_sendsStatusCode502ToClient() throws Exception {

        doThrow(Exception.class).when(httpMessageHandler).handle(any(), any(), any());

        try {
            handler.handleResponse(source, target, configuration, callback);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(HttpStatusCode.BAD_GATEWAY);
        }

    }

    @Test
    public void handleRequest_whenAnUnexpectedErrorOccurredWhileParsingHeader_treatErrorAsInvalidResponseHeaderError()
            throws  Exception{

        doThrow(new ExecutionException(new NullPointerException())).when(future).get(anyLong(), any());

        try {
            handler.handleResponse(source, target, configuration, callback);
            fail("IOException expected.");
        } catch (final IOException ex) {
            assertHttpStatusCode(HttpStatusCode.BAD_GATEWAY);
        }

    }

    @Test(expected = IOException.class)
    public void handleResponse_whenAnErrorOccurredWhileSendingStatusCodeToClient_ignoresNewError()
            throws Exception {
        doThrow(new ExecutionException(new IOException())).when(future).get(anyLong(), any());
        when(source.getOutputStream()).thenReturn(null);
        handler.handleResponse(source, target, configuration, callback);
    }

    // Invalid parameters:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderParser_throwsNullPointerException() {
        new HttpExchangeHandlerImpl(null, httpMessageHandler, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpMessageHandler_throwsNullPointerException() {
        new HttpExchangeHandlerImpl(httpHeaderParser, null, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new HttpExchangeHandlerImpl(httpHeaderParser, httpMessageHandler, null);
    }

    @Test(expected = NullPointerException.class)
    public void handleRequest_withoutSource_throwsNullPointerException() throws Exception {
        handler.handleRequest(null, target, configuration, callback);
    }

    @Test(expected = NullPointerException.class)
    public void handleRequest_withoutTarget_throwsNullPointerException() throws Exception {
        handler.handleRequest(source, null, configuration, callback);
    }

    @Test(expected = NullPointerException.class)
    public void handleRequest_withoutConfiguration_throwsNullPointerException() throws Exception {
        handler.handleRequest(source, target, null, callback);
    }

    @Test(expected = NullPointerException.class)
    public void handleRequest_withoutCallback_throwsNullPointerException() throws Exception {
        handler.handleRequest(source, target, configuration, null);
    }

    @Test(expected = NullPointerException.class)
    public void handleResponse_withoutSource_throwsNullPointerException() throws Exception {
        handler.handleResponse(null, target, configuration, callback);
    }

    @Test(expected = NullPointerException.class)
    public void handleResponse_withoutTarget_throwsNullPointerException() throws Exception {
        handler.handleResponse(source, null, configuration, callback);
    }

    @Test(expected = NullPointerException.class)
    public void handleResponse_withoutConfiguration_throwsNullPointerException() throws Exception {
        handler.handleResponse(source, target, null, callback);
    }

    @Test(expected = NullPointerException.class)
    public void handleResponse_withoutCallback_throwsNullPointerException() throws Exception {
        handler.handleResponse(source, target, configuration, null);
    }

    // Helper methods:

    private void assertHttpStatusCode(final HttpStatusCode statusCode) {
        final String output = sourceOutputStream.toString();
        assertTrue(output.contains(Integer.toString(statusCode.getCode())));
        assertTrue(output.contains(statusCode.getReason()));
    }

}
