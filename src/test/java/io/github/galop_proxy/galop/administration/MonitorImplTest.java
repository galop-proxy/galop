package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.configuration.HttpConnectionConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link MonitorImpl}.
 */
public class MonitorImplTest {

    private HttpConnectionConfiguration configuration;
    private ExecutorService executorService;
    private Monitor monitor;

    @Before
    public void setUp() {
        configuration = mock(HttpConnectionConfiguration.class);
        when(configuration.getLogInterval()).thenReturn(100L);
        executorService = spy(Executors.newCachedThreadPool());
        monitor = new MonitorImpl(configuration, executorService);
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

        while (monitor.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException ex) {
                fail("Unexpected InterruptedException.");
                break;
            }
        }

    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new MonitorImpl(null, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new MonitorImpl(configuration, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_withInvalidExecutorService_throwsIllegalArgumentException() {
        new MonitorImpl(configuration, mock(ExecutorService.class));
    }

}
