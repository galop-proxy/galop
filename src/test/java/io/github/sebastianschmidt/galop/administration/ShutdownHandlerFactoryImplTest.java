package io.github.sebastianschmidt.galop.administration;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.proxy.Server;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link ShutdownHandlerFactoryImpl}.
 */
public class ShutdownHandlerFactoryImplTest {

    private ShutdownHandlerFactoryImpl factory;

    @Before
    public void setUp() {
        final ExecutorService executorService = mock(ExecutorService.class);
        factory = new ShutdownHandlerFactoryImpl(executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new ShutdownHandlerFactoryImpl(null);
    }

    @Test
    public void create_returnsShutdownThread() {
        assertNotNull(factory.create(mock(Configuration.class), mock(Server.class), mock(Thread.class)));
    }

}
