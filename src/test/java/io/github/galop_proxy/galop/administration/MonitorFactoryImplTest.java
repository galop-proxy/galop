package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link MonitorFactoryImpl}.
 */
public class MonitorFactoryImplTest {

    private MonitorFactoryImpl factory;
    private Configuration configuration;

    @Before
    public void setUp() {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        factory = new MonitorFactoryImpl(executorService);
        configuration = mock(Configuration.class);
        when(configuration.getHttpConnectionLogInterval()).thenReturn(10000L);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new MonitorFactoryImpl(null);
    }

    @Test
    public void create_withConfiguration_returnsMonitorThread() {
        assertNotNull(factory.create(configuration));
    }

    @Test
    public void create_withConfiguration_returnsNotStartedThread() {
        assertEquals(Thread.State.NEW, factory.create(configuration).getState());
        assertFalse(factory.create(configuration).isAlive());
        assertFalse(factory.create(configuration).isInterrupted());
    }

    @Test(expected = NullPointerException.class)
    public void create_withoutConfiguration_throwsNullPointerException() {
        factory.create(null);
    }

}
