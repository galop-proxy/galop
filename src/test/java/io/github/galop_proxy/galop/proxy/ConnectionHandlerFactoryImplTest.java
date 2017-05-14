package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.HttpExchangeHandler;
import io.github.galop_proxy.galop.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link ConnectionHandlerFactoryImpl}.
 */
public class ConnectionHandlerFactoryImplTest {
    
    private ConnectionHandlerFactoryImpl factory;

    @Before
    public void setUp() {
        final HttpExchangeHandler handler = mock(HttpExchangeHandler.class);
        factory = new ConnectionHandlerFactoryImpl(handler);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpExchangeHandler_throwsNullPointerException() {
        new ConnectionHandlerFactoryImpl(null);
    }

    @Test
    public void create_returnsRunnableConnectionHandler() {
        final Configuration configuration = mock(Configuration.class);
        final Socket source = mock(Socket.class);
        final Socket target = mock(Socket.class);
        assertNotNull(factory.create(configuration, source, target));
    }

}
