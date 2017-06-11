package io.github.galop_proxy.galop.administration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.AbstractConfigurationTest;
import io.github.galop_proxy.galop.network.NetworkModule;
import io.github.galop_proxy.galop.configuration.LoadedConfigurationModule;
import io.github.galop_proxy.galop.http.HttpModule;
import io.github.galop_proxy.galop.proxy.ProxyModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link AdministrationModule}.
 */
public class AdministrationModuleTest extends AbstractConfigurationTest {

    private Injector injector;

    @Before
    public void setUp() {
        super.setUp();
        injector = Guice.createInjector(new NetworkModule(), new LoadedConfigurationModule(configuration),
                new HttpModule(), new ProxyModule(), new AdministrationModule());
    }

    @Test
    public void configure_bindsMonitor() {
        assertNotNull(injector.getInstance(Monitor.class));
    }

    @Test
    public void configure_bindsMonitorAsSingleton() {
        assertSame(injector.getInstance(Monitor.class),
                injector.getInstance(Monitor.class));
    }

    @Test
    public void configure_bindsShutdownHandler() {
        assertNotNull(injector.getInstance(ShutdownHandler.class));
    }

    @Test
    public void configure_bindsShutdownHandlerAsSingleton() {
        assertSame(injector.getInstance(ShutdownHandler.class),
                injector.getInstance(ShutdownHandler.class));
    }

}
