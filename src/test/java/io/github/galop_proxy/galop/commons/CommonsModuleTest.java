package io.github.galop_proxy.galop.commons;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link CommonsModule}.
 */
public class CommonsModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new CommonsModule());
    }

    @Test
    public void configure_bindsCurrentRuntime() {
        assertSame(Runtime.getRuntime(), injector.getInstance(Runtime.class));
    }

    @Test
    public void configure_bindsInetAddressFactory() {
        assertNotNull(injector.getInstance(InetAddressFactory.class));
    }

    @Test
    public void configure_bindsInetAddressFactoryAsSingleton() {
        assertSame(injector.getInstance(InetAddressFactory.class), injector.getInstance(InetAddressFactory.class));
    }

    @Test
    public void configure_bindsExecutorService() {
        assertNotNull(injector.getInstance(ExecutorService.class));
    }

    @Test
    public void configure_bindsExecutorServiceAsSingleton() {
        assertSame(injector.getInstance(ExecutorService.class),
                injector.getInstance(ExecutorService.class));
    }

}
