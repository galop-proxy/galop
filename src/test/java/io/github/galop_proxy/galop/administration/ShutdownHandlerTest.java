package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.proxy.Server;
import io.github.galop_proxy.galop.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ShutdownHandler}.
 */
public class ShutdownHandlerTest {

    private static final long TERMINATION_TIMEOUT = 30000;

    private Configuration configuration;
    private Server server;
    private ExecutorService executorService;
    private Thread monitor;
    private ShutdownHandler shutdownHandler;

    @Before
    public void setUp() throws InterruptedException {
        configuration = mock(Configuration.class);
        when(configuration.getHttpConnectionTerminationTimeout()).thenReturn(TERMINATION_TIMEOUT);
        server = mock(Server.class);
        executorService = mock(ExecutorService.class);
        when(executorService.awaitTermination(anyLong(), any())).thenReturn(true);
        monitor = mock(Thread.class);
        shutdownHandler = new ShutdownHandler(configuration, server, executorService, monitor);
    }

    @Test
    public void run_terminatesInCorrectOrder() throws Exception {
        shutdownHandler.run();
        assertEverythingTerminatedInCorrectOrder();
    }

    @Test
    public void run_whenServerTerminationFails_continuesTerminatingInCorrectOrder() throws Exception {
        doThrow(IOException.class).when(server).close();
        shutdownHandler.run();
        assertEverythingTerminatedInCorrectOrder();
    }

    @Test
    public void run_whenConnectionHandlersCanNotBeTerminatedBeforeTimeout_continuesTerminatingInCorrectOrder()
            throws Exception {
        when(executorService.awaitTermination(anyLong(), any())).thenReturn(false);
        shutdownHandler.run();
        assertEverythingTerminatedInCorrectOrder();
    }

    @Test
    public void run_whenConnectionHandlersTerminationFails_continuesTerminatingInCorrectOrder() throws Exception {
        doThrow(RuntimeException.class).when(executorService).awaitTermination(anyLong(), any());
        shutdownHandler.run();
        assertEverythingTerminatedInCorrectOrder();
    }

    @Test
    public void run_whenMonitorTerminationFails_continuesTerminatingInCorrectOrder() throws Exception {
        doThrow(RuntimeException.class).when(monitor).interrupt();
        shutdownHandler.run();
        assertEverythingTerminatedInCorrectOrder();
    }

    private void assertEverythingTerminatedInCorrectOrder() throws Exception {
        final InOrder inOrder = inOrder(server, executorService, monitor);
        inOrder.verify(server).close();
        inOrder.verify(executorService).shutdownNow();
        inOrder.verify(executorService).awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
        inOrder.verify(monitor).interrupt();
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new ShutdownHandler(null, server, executorService, monitor);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutServer_throwsNullPointerException() {
        new ShutdownHandler(configuration, null, executorService, monitor);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new ShutdownHandler(configuration, server, null, monitor);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutMonitor_throwsNullPointerException() {
        new ShutdownHandler(configuration, server, executorService, null);
    }

}
