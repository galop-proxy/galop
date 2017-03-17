package io.github.sebastianschmidt.galop.administration;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link Monitor}.
 */
public class MonitorTest {

    private Configuration configuration;
    private ExecutorService executorService;
    private Monitor monitor;

    @Before
    public void setUp() {
        configuration = mock(Configuration.class);
        when(configuration.getConnectionHandlersLogInterval()).thenReturn(100L);
        executorService = spy(Executors.newCachedThreadPool());
        monitor = new Monitor(configuration, executorService);
    }

    @Test
    public void start_logsCurrentNumberOfActiveConnectionHandlers() {
        monitor.start();
        verify(((ThreadPoolExecutor) executorService), timeout(10000).atLeast(2)).getActiveCount();
        monitor.interrupt();
    }

    @Test(timeout = 10000)
    public void interrupt_shutDownsMonitor() {

        monitor.start();
        verify(((ThreadPoolExecutor) executorService), timeout(10000).atLeastOnce()).getActiveCount();

        monitor.interrupt();

        assertTrue(monitor.isInterrupted());
        while (monitor.isAlive());

    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new Monitor(null, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new Monitor(configuration, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withInvalidExecutorService_throwsIllegalArgumentException() {
        new Monitor(configuration, mock(ExecutorService.class));
    }

}
