package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.http.HttpHeaderParser;
import io.github.sebastianschmidt.galop.http.HttpMessageHandler;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link ConnectionHandlerFactoryImpl}.
 */
public class ConnectionHandlerFactoryImplTest {

    private HttpHeaderParser parser;
    private HttpMessageHandler handler;
    private ConnectionHandlerFactoryImpl factory;

    @Before
    public void setUp() {
        parser = mock(HttpHeaderParser.class);
        handler = mock(HttpMessageHandler.class);
        factory = new ConnectionHandlerFactoryImpl(parser, handler);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderParser_throwsNullPointerException() {
        new ConnectionHandlerFactoryImpl(null, handler);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpMessageHandler_throwsNullPointerException() {
        new ConnectionHandlerFactoryImpl(parser, null);
    }

    @Test
    public void create_returnsRunnableConnectionHandler() {
        final Configuration configuration = mock(Configuration.class);
        final Socket source = mock(Socket.class);
        final Socket target = mock(Socket.class);
        assertNotNull(factory.create(configuration, source, target));
    }

}
