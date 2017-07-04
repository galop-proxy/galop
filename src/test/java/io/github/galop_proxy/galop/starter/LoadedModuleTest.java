package io.github.galop_proxy.galop.starter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.AbstractConfigurationTest;
import io.github.galop_proxy.galop.administration.Monitor;
import io.github.galop_proxy.galop.network.NetworkModule;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.http.ExchangeHandler;
import io.github.galop_proxy.galop.proxy.Server;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link LoadedModule}.
 */
public class LoadedModuleTest extends AbstractConfigurationTest {

    private Injector injector;

    @Before
    public void setUp() {
        super.setUp();
        injector = Guice.createInjector(new NetworkModule(), new LoadedModule(configuration));
    }

    @Test
    public void configure_bindsLoadedConfigurationModule() {
        assertNotNull(injector.getInstance(Configuration.class));
    }

    @Test
    public void configure_bindsHttpModule() {
        assertNotNull(injector.getInstance(ExchangeHandler.class));
    }

    @Test
    public void configure_bindsProxyModule() {
        assertNotNull(injector.getInstance(Server.class));
    }

    @Test
    public void configure_bindsAdministrationModule() {
        assertNotNull(injector.getInstance(Monitor.class));
    }

    @Test
    public void configure_bindsStarter() {
        assertNotNull(injector.getInstance(Starter.class));
    }

    @Test
    public void configure_bindsStarterAsSingleton() {
        assertSame(injector.getInstance(Starter.class), injector.getInstance(Starter.class));
    }

}
