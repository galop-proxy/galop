package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.commons.ServerSocketFactory;
import io.github.sebastianschmidt.galop.commons.SocketFactory;
import io.github.sebastianschmidt.galop.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link ServerFactoryImpl}.
 */
public class ServerFactoryImplTest {

    private ServerSocketFactory serverSocketFactory;
    private SocketFactory socketFactory;
    private ConnectionHandlerFactory connectionHandlerFactory;
    private ExecutorService executorService;
    private ServerFactoryImpl factory;

    @Before
    public void setUp() {
        serverSocketFactory = mock(ServerSocketFactory.class);
        socketFactory = mock(SocketFactory.class);
        connectionHandlerFactory = mock(ConnectionHandlerFactory.class);
        executorService = mock(ExecutorService.class);
        factory = new ServerFactoryImpl(serverSocketFactory, socketFactory, connectionHandlerFactory, executorService);
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void construct_withoutServerSocketFactory_throwsNullPointerException() {
        new ServerFactoryImpl(null, socketFactory, connectionHandlerFactory, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void construct_withoutSocketFactory_throwsNullPointerException() {
        new ServerFactoryImpl(serverSocketFactory, null, connectionHandlerFactory, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void construct_withoutConnectionHandlerFactory_throwsNullPointerException() {
        new ServerFactoryImpl(serverSocketFactory, socketFactory, null, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void construct_withoutExecutorService_throwsNullPointerException() {
        new ServerFactoryImpl(serverSocketFactory, socketFactory, connectionHandlerFactory, null);
    }

    // Create:

    @Test
    public void create_withConfiguration_returnsServerInstance() {
        assertNotNull(factory.create(mock(Configuration.class)));
    }

    @Test(expected = NullPointerException.class)
    public void create_withoutConfiguration_throwsNullPointerException() {
        factory.create(null);
    }

}
