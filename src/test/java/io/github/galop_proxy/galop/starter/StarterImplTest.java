package io.github.galop_proxy.galop.starter;

import io.github.galop_proxy.galop.administration.Monitor;
import io.github.galop_proxy.galop.administration.ShutdownHandler;
import io.github.galop_proxy.galop.proxy.Server;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the class {@link StarterImpl}.
 */
public class StarterImplTest {

    private Runtime runtime;
    private ShutdownHandler shutdownHandler;
    private Monitor monitor;
    private Server server;
    private Starter starter;

    @Before
    public void setUp() {
        runtime = mock(Runtime.class);
        shutdownHandler = mock(ShutdownHandler.class);
        monitor = mock(Monitor.class);
        server = mock(Server.class);
        starter = new StarterImpl(runtime, shutdownHandler, monitor, server);
    }

    // start:

    @Test
    public void start_withoutErrors_startsGalop() {
        starter.start();
        verify(runtime).addShutdownHook(any());
        verify(monitor).start();
        verify(server).run();
    }

    @Test
    public void start_withErrors_exitsApplication() {
        doThrow(RuntimeException.class).when(server).run();
        starter.start();
        verify(runtime).exit(1);
    }

    // constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutRuntime_throwsNullPointerException() {
        new StarterImpl(null, shutdownHandler, monitor, server);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutShutdownHandler_throwsNullPointerException() {
        new StarterImpl(runtime, null, monitor, server);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutMonitor_throwsNullPointerException() {
        new StarterImpl(runtime, shutdownHandler, null, server);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutServer_throwsNullPointerException() {
        new StarterImpl(runtime, shutdownHandler, monitor, null);
    }

}
