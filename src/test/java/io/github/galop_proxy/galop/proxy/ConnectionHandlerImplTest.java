package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.http.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ConnectionHandlerImpl}.
 */
public class ConnectionHandlerImplTest {

    private HttpExchangeHandler httpExchangeHandler;
    private Configuration configuration;
    private Socket source;
    private Socket target;

    private ConnectionHandlerImpl handler;

    @Before
    public void setUp() throws Exception {

        configuration = mock(Configuration.class);

        httpExchangeHandler = mock(HttpExchangeHandler.class);
        doAnswer(this::answerHandler).when(httpExchangeHandler).handleRequest(any(), any(), any(), any());
        doAnswer(this::answerHandler).when(httpExchangeHandler).handleResponse(any(), any(), any(), any());

        source = mock(Socket.class);
        when(source.isClosed()).thenReturn(false);
        target = mock(Socket.class);
        when(target.isClosed()).thenReturn(false);

        handler = new ConnectionHandlerImpl(httpExchangeHandler, configuration, source, target);

    }

    private Object answerHandler(final InvocationOnMock invocation) {
        final Runnable callback = (Runnable) invocation.getArguments()[3];
        callback.run();
        return null;
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpExchangeHandler_throwsNullPointerException() {
        new ConnectionHandlerImpl(null, configuration, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new ConnectionHandlerImpl(httpExchangeHandler, null, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutSourceSocket_throwsNullPointerException() {
        new ConnectionHandlerImpl(httpExchangeHandler, configuration, null, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetSocket_throwsNullPointerException() {
        new ConnectionHandlerImpl(httpExchangeHandler, configuration, source, null);
    }

    // Handle requests and responses:

    @Test
    public void run_withOneRequest_callsHttpExchangeHandler() throws Exception {
        when(source.isClosed()).thenReturn(false).thenReturn(true);
        handler.run();
        verify(httpExchangeHandler).handleRequest(same(source), same(target), same(configuration), any());
        verify(httpExchangeHandler).handleResponse(same(source), same(target), same(configuration), any());
    }

    @Test
    public void run_withTwoRequests_callsHttpExchangeHandlerTwoTimes() throws Exception {
        when(source.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);
        handler.run();
        verify(httpExchangeHandler, times(2)).handleRequest(same(source), same(target), same(configuration), any());
        verify(httpExchangeHandler, times(2)).handleResponse(same(source), same(target), same(configuration), any());
    }

    @Test(timeout = 5000)
    public void run_whenHttpExchangeHandlerThrowsException_closesSocketsAndTerminates() throws Exception {
        doThrow(Exception.class).when(httpExchangeHandler).handleRequest(any(), any(), any(), any());
        handler.run();
        verify(source).close();
        verify(target).close();
    }

    // Closed sockets:

    @Test
    public void run_whenSourceSocketIsClosed_closesTargetSocketAndTerminates() throws Exception {
        when(source.isClosed()).thenReturn(true);
        handler.run();
        verify(target).close();
    }

    @Test
    public void run_whenTargetSocketIsClosed_closesSourceSocketAndTerminates() throws Exception {
        when(target.isClosed()).thenReturn(true);
        handler.run();
        verify(source).close();
    }

    // Interrupted execution:

    @Test
    public void run_whenThreadIsInterrupted_closesSourceAndTargetSocketAndTerminates() throws Exception {

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(handler);

        executorService.shutdownNow();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        verify(source, atLeastOnce()).close();
        verify(target, atLeastOnce()).close();

    }

    // Close handler:

    @Test
    public void close_whenCurrentlyNoRequestOrResponseIsHandled_closesSocketsAndTerminates() throws Exception {

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(handler);

        handler.close();

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        verify(source, atLeastOnce()).close();
        verify(target, atLeastOnce()).close();

    }

}
