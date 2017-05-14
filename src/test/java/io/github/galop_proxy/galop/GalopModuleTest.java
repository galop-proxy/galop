package io.github.galop_proxy.galop;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.administration.MonitorFactory;
import io.github.galop_proxy.galop.commons.ServerSocketFactory;
import io.github.galop_proxy.galop.http.HttpExchangeHandler;
import io.github.galop_proxy.galop.proxy.ServerFactory;
import io.github.galop_proxy.galop.configuration.ConfigurationFileLoader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link GalopModule}.
 */
public class GalopModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new GalopModule());
    }

    @Test
    public void configure_installsCommonsModule() {
        assertNotNull(injector.getInstance(ServerSocketFactory.class));
    }

    @Test
    public void configure_installsConfigurationModule() {
        assertNotNull(injector.getInstance(ConfigurationFileLoader.class));
    }

    @Test
    public void configure_installsParserModule() {
        assertNotNull(injector.getInstance(HttpExchangeHandler.class));
    }

    @Test
    public void configure_installsProxyModule() {
        assertNotNull(injector.getInstance(ServerFactory.class));
    }

    @Test
    public void configure_installsAdministrationModule() {
        assertNotNull(injector.getInstance(MonitorFactory.class));
    }

    @Test
    public void configure_bindsStarter() {
        assertNotNull(injector.getInstance(Starter.class));
    }

}
