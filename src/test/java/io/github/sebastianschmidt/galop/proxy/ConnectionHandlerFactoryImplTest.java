package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.parser.HttpHeaderParser;
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
        final HttpHeaderParser parser = mock(HttpHeaderParser.class);
        factory = new ConnectionHandlerFactoryImpl(parser);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderParser_throwsNullPointerException() {
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
