package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ConnectionHandlerImpl}.
 */
public class ConnectionHandlerImplTest {

    private ExchangeHandler exchangeHandler;
    private Socket source;
    private Socket target;

    private ConnectionHandlerImpl handler;

    @Before
    public void setUp() throws Exception {

        exchangeHandler = mock(ExchangeHandler.class);
        source = mock(Socket.class);
        target = mock(Socket.class);

        handler = new ConnectionHandlerImpl(exchangeHandler, source, target);

    }

    // constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpExchangeHandler_throwsNullPointerException() {
        new ConnectionHandlerImpl(null, source, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutSourceSocket_throwsNullPointerException() {
        new ConnectionHandlerImpl(exchangeHandler, null, target);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetSocket_throwsNullPointerException() {
        new ConnectionHandlerImpl(exchangeHandler, source, null);
    }

    // run:

    @Test
    public void run_handlesRequestAndThenResponseAndThenClosesSockets() throws Exception {

        handler.run();

        final InOrder inOrder = inOrder(exchangeHandler, source, target);

        inOrder.verify(exchangeHandler).handleRequest(source, target);
        inOrder.verify(exchangeHandler).handleResponse(source, target);

        inOrder.verify(source).close();
        inOrder.verify(target).close();

    }

    @Test
    public void run_whenExchangeHandlerThrowsException_closesSockets() throws Exception {

        doThrow(Exception.class).when(exchangeHandler).handleRequest(source, target);

        handler.run();

        verify(exchangeHandler).handleRequest(source, target);
        verify(exchangeHandler, never()).handleResponse(source, target);

        verify(source).close();
        verify(target).close();

    }

    // close:

    @Test
    public void close_never_closesSocketsAgain() throws IOException {

        handler.run();

        handler.close();

        verify(source).close();
        verify(target).close();

    }

}
