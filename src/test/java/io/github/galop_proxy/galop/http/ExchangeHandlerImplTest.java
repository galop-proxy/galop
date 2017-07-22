package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.galop.configuration.HttpHeaderConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderRequestConfiguration;
import io.github.galop_proxy.galop.configuration.HttpHeaderResponseConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ExchangeHandlerImpl}.
 */
public class ExchangeHandlerImplTest {

    private final static long REQUEST_TIMEOUT = 10000;
    private final static long RESPONSE_TIMEOUT = 20000;

    private HttpHeaderConfiguration configuration;
    private Request request;
    private Response response;

    private MessageParser messageParser;
    private MessageWriter messageWriter;
    private Future<Message> future;

    private ExecutorService executorService;
    private ExchangeHandlerImpl handler;

    private Socket source;
    private InputStream sourceInputStream;
    private OutputStream sourceOutputStream;

    private Socket target;
    private InputStream targetInputStream;
    private OutputStream targetOutputStream;

    @Before
    public void setUp() throws Exception {

        configuration = mockConfiguration();
        messageParser = mockMessageParser();
        messageWriter = mock(MessageWriter.class);
        executorService = mockExecutorService();
        handler = new ExchangeHandlerImpl(configuration, messageParser, messageWriter, executorService);

        source = mock(Socket.class);
        sourceInputStream = spy(new ByteArrayInputStream(new byte[0]));
        sourceOutputStream = spy(new ByteArrayOutputStream());
        when(source.getInputStream()).thenReturn(sourceInputStream);
        when(source.getOutputStream()).thenReturn(sourceOutputStream);

        target = mock(Socket.class);
        targetInputStream = spy(new ByteArrayInputStream(new byte[0]));
        targetOutputStream = spy(new ByteArrayOutputStream());
        when(target.getInputStream()).thenReturn(targetInputStream);
        when(target.getOutputStream()).thenReturn(targetOutputStream);

    }

    private HttpHeaderConfiguration mockConfiguration() {

        final HttpHeaderRequestConfiguration requestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        when(requestConfiguration.getReceiveTimeout()).thenReturn(REQUEST_TIMEOUT);

        final HttpHeaderResponseConfiguration responseConfiguration = mock(HttpHeaderResponseConfiguration.class);
        when(responseConfiguration.getReceiveTimeout()).thenReturn(RESPONSE_TIMEOUT);

        final HttpHeaderConfiguration configuration = mock(HttpHeaderConfiguration.class);
        when(configuration.getRequest()).thenReturn(requestConfiguration);
        when(configuration.getResponse()).thenReturn(responseConfiguration);
        return configuration;

    }

    private MessageParser mockMessageParser() throws IOException {

        final MessageParser parser = mock(MessageParser.class);

        request = mock(Request.class);
        when(parser.parseRequest(any())).thenReturn(request);

        response = mock(Response.class);
        when(parser.parseResponse(any())).thenReturn(response);

        return parser;

    }

    @SuppressWarnings("unchecked")
    private ExecutorService mockExecutorService() throws Exception {

        future = mock(Future.class);
        when(future.get(anyLong(), any())).thenReturn(request);

        final ExecutorService executorService = mock(ExecutorService.class);
        doAnswer(invocation -> {
            final Callable<?> callable = (Callable<?>) invocation.getArguments()[0];
            callable.call();
            return future;
        }).when(executorService).submit((Callable<?>) any());

        return executorService;

    }

    // Handle request:

    @Test
    public void handleRequest_withoutClientOrServerErrors_callsParserAndHandler() throws Exception {
        handler.handleRequest(source, target);
        verify(messageParser).parseRequest(sourceInputStream);
        verify(messageWriter).writeRequest(request, sourceInputStream, targetOutputStream);
        verify(future).get(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Test(expected = UnsupportedTransferEncodingException.class)
    public void handleRequest_withUnsupportedTransferEncoding_sendsStatusCode411ToClient() throws Exception {

        doThrow(UnsupportedTransferEncodingException.class).when(messageParser).parseRequest(same(sourceInputStream));

        try {
            handler.handleRequest(source, target);
        } catch (final UnsupportedTransferEncodingException ex) {
            assertHttpStatusCode(StatusCode.LENGTH_REQUIRED);
            throw ex;
        }

    }

    @Test(expected = UnsupportedHttpVersionException.class)
    public void handleRequest_withUnsupportedHttpVersion_sendsStatusCode505ToClient() throws Exception {

        doThrow(UnsupportedHttpVersionException.class).when(messageParser).parseRequest(same(sourceInputStream));

        try {
            handler.handleRequest(source, target);
        } catch (final UnsupportedHttpVersionException ex) {
            assertHttpStatusCode(StatusCode.HTTP_VERSION_NOT_SUPPORTED);
            throw ex;
        }

    }

    @Test(expected = ByteLimitExceededException.class)
    public void handleRequest_withTooLongRequestHeader_sendsStatusCode431ToClient() throws Exception {

        doThrow(ByteLimitExceededException.class).when(messageParser).parseRequest(same(sourceInputStream));

        try {
            handler.handleRequest(source, target);
        } catch (final ByteLimitExceededException ex) {
            assertHttpStatusCode(StatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE);
            throw ex;
        }

    }

    @Test(expected = HeaderFieldsTooLargeException.class)
    public void handleRequest_withTooManyHeaderFields_sendsStatusCode431ToClient() throws Exception {

        doThrow(HeaderFieldsTooLargeException.class).when(messageParser).parseRequest(same(sourceInputStream));

        try {
            handler.handleRequest(source, target);
        } catch (final HeaderFieldsTooLargeException ex) {
            assertHttpStatusCode(StatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE);
            throw ex;
        }

    }

    @Test
    public void handleRequest_withInvalidRequestHeader_sendsStatusCode400ToClient() throws Exception {

        doThrow(Exception.class).when(messageParser).parseRequest(same(sourceInputStream));

        try {
            handler.handleRequest(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.BAD_REQUEST);
        }

    }

    @Test
    public void handleRequest_whenInterrupted_sendsStatusCode503ToClient() throws Exception {

        doThrow(InterruptedException.class).when(future).get(anyLong(), any());

        try {
            handler.handleRequest(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.SERVICE_UNAVAILABLE);
        }

    }

    @Test
    public void handleRequest_whenReceiveRequestHeaderTimeout_sendsStatusCode408ToClient() throws Exception {

        doThrow(TimeoutException.class).when(future).get(anyLong(), any());

        try {
            handler.handleRequest(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.REQUEST_TIMEOUT);
        }

    }

    @Test
    public void handleRequest_whenAnErrorOccurredWhileSendingToServer_sendsStatusCode400ToClient() throws Exception {

        doThrow(Exception.class).when(messageWriter).writeRequest(request, sourceInputStream, targetOutputStream);

        try {
            handler.handleRequest(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.BAD_REQUEST);
        }

    }

    @Test
    public void handleRequest_whenAnUnexpectedErrorOccurredWhileParsingHeader_treatErrorAsInvalidRequestHeaderError()
            throws  Exception{

        doThrow(new ExecutionException(new NullPointerException())).when(future).get(anyLong(), any());

        try {
            handler.handleRequest(source, target);
            fail("IOException expected.");
        } catch (final IOException ex) {
            assertHttpStatusCode(StatusCode.BAD_REQUEST);
        }

    }

    @Test(expected = IOException.class)
    public void handleRequest_whenAnErrorOccurredWhileSendingStatusCodeToClient_ignoresNewError()
            throws Exception {
        doThrow(new ExecutionException(new IOException())).when(future).get(anyLong(), any());
        when(source.getOutputStream()).thenReturn(null);
        handler.handleRequest(source, target);
    }

    // Handle response:

    @Test
    public void handleResponse_withoutClientOrServerErrors_callsParserAndHandler() throws Exception {

        when(future.get(anyLong(), any())).thenReturn(response);

        handler.handleResponse(source, target);

        verify(messageParser).parseResponse(same(targetInputStream));
        verify(messageWriter).writeResponse(response, targetInputStream, sourceOutputStream);
        verify(future).get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);

    }

    @Test
    public void handleResponse_withInvalidResponseHeader_sendsStatusCode502ToClient() throws Exception {

        doThrow(Exception.class).when(messageParser).parseResponse(same(targetInputStream));

        try {
            handler.handleResponse(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.BAD_GATEWAY);
        }

    }

    @Test(expected = UnsupportedHttpVersionException.class)
    public void handleResponse_withUnsupportedHttpVersion_sendsStatusCode502ToClient() throws Exception {

        doThrow(UnsupportedHttpVersionException.class).when(messageParser).parseResponse(same(targetInputStream));

        try {
            handler.handleResponse(source, target);
        } catch (final UnsupportedHttpVersionException ex) {
            assertHttpStatusCode(StatusCode.BAD_GATEWAY);
            throw ex;
        }

    }

    @Test(expected = UnsupportedStatusCodeException.class)
    public void handleResponse_withUnsupportedStatusCode_sendsStatusCode502ToClient() throws Exception {

        doThrow(UnsupportedStatusCodeException.class).when(messageParser).parseResponse(same(targetInputStream));

        try {
            handler.handleResponse(source, target);
        } catch (final UnsupportedStatusCodeException ex) {
            assertHttpStatusCode(StatusCode.BAD_GATEWAY);
            throw ex;
        }

    }

    @Test
    public void handleResponse_whenInterrupted_sendsStatusCode503ToClient() throws Exception {

        doThrow(InterruptedException.class).when(future).get(anyLong(), any());

        try {
            handler.handleResponse(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.SERVICE_UNAVAILABLE);
        }

    }

    @Test
    public void handleResponse_whenReceiveResponseHeaderTimeout_sendsStatusCode504ToClient() throws Exception {

        doThrow(TimeoutException.class).when(future).get(anyLong(), any());

        try {
            handler.handleResponse(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.GATEWAY_TIMEOUT);
        }

    }

    @Test
    public void handleResponse_whenAnErrorOccurredBeforeSendingToClient_sendsStatusCode502ToClient() throws Exception {

        doThrow(Exception.class).when(messageParser).parseResponse(same(targetInputStream));

        try {
            handler.handleResponse(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertHttpStatusCode(StatusCode.BAD_GATEWAY);
        }

    }

    @Test
    public void handleResponse_whenAnErrorOccurredWhileSendingToClient_sendsNoStatusCodeToClient() throws Exception {

        when(future.get(anyLong(), any())).thenReturn(response);

        doThrow(Exception.class).when(messageWriter).writeResponse(response, targetInputStream, sourceOutputStream);

        try {
            handler.handleResponse(source, target);
            fail("Exception expected.");
        } catch (final Exception ex) {
            assertTrue(sourceOutputStream.toString().isEmpty());
        }

    }

    @Test
    public void handleResponse_whenAnUnexpectedErrorOccurredWhileParsingHeader_treatErrorAsInvalidResponseHeaderError()
            throws Exception {

        doThrow(new ExecutionException(new NullPointerException())).when(future).get(anyLong(), any());

        try {
            handler.handleResponse(source, target);
            fail("IOException expected.");
        } catch (final IOException ex) {
            assertHttpStatusCode(StatusCode.BAD_GATEWAY);
        }

    }

    @Test(expected = IOException.class)
    public void handleResponse_whenAnErrorOccurredWhileSendingStatusCodeToClient_ignoresNewError()
            throws Exception {
        doThrow(new ExecutionException(new IOException())).when(future).get(anyLong(), any());
        when(source.getOutputStream()).thenReturn(null);
        handler.handleResponse(source, target);
    }

    // Invalid parameters:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderConfiguration_throwsNullPointerException() {
        new ExchangeHandlerImpl(null, messageParser, messageWriter, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutMessageParser_throwsNullPointerException() {
        new ExchangeHandlerImpl(configuration, null, messageWriter, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpMessageHandler_throwsNullPointerException() {
        new ExchangeHandlerImpl(configuration, messageParser, null, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new ExchangeHandlerImpl(configuration, messageParser, messageWriter, null);
    }

    @Test(expected = NullPointerException.class)
    public void handleRequest_withoutSource_throwsNullPointerException() throws Exception {
        handler.handleRequest(null, target);
    }

    @Test(expected = NullPointerException.class)
    public void handleRequest_withoutTarget_throwsNullPointerException() throws Exception {
        handler.handleRequest(source, null);
    }

    @Test(expected = NullPointerException.class)
    public void handleResponse_withoutSource_throwsNullPointerException() throws Exception {
        handler.handleResponse(null, target);
    }

    @Test(expected = NullPointerException.class)
    public void handleResponse_withoutTarget_throwsNullPointerException() throws Exception {
        handler.handleResponse(source, null);
    }

    // Helper methods:

    private void assertHttpStatusCode(final StatusCode statusCode) {
        final String output = sourceOutputStream.toString();
        assertTrue(output.contains(Integer.toString(statusCode.getCode())));
        assertTrue(output.contains(statusCode.getReason()));
    }

}
