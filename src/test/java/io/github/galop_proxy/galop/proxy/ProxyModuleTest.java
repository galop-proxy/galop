package io.github.galop_proxy.galop.proxy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.AbstractConfigurationTest;
import io.github.galop_proxy.galop.commons.CommonsModule;
import io.github.galop_proxy.galop.configuration.LoadedConfigurationModule;
import io.github.galop_proxy.galop.http.HttpModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link ProxyModule}.
 */
public class ProxyModuleTest extends AbstractConfigurationTest {

    private Injector injector;

    @Before
    public void setUp() {
        super.setUp();
        injector = Guice.createInjector(
                new CommonsModule(), new LoadedConfigurationModule(configuration), new HttpModule(), new ProxyModule());
    }

    @Test
    public void configure_bindsProxySocketFactory() {
        assertNotNull(injector.getInstance(ProxySocketFactory.class));
    }

    @Test
    public void configure_bindsProxySocketFactoryAsSingleton() {
        assertSame(injector.getInstance(ProxySocketFactory.class), injector.getInstance(ProxySocketFactory.class));
    }

    @Test
    public void configure_bindsTargetSocketFactory() {
        assertNotNull(injector.getInstance(TargetSocketFactory.class));
    }

    @Test
    public void configure_bindsTargetSocketFactoryAsSingleton() {
        assertSame(injector.getInstance(TargetSocketFactory.class), injector.getInstance(TargetSocketFactory.class));
    }

    @Test
    public void configure_bindsConnectionHandlerFactory() {
        assertNotNull(injector.getInstance(ConnectionHandlerFactory.class));
    }

    @Test
    public void configure_bindsConnectionHandlerFactoryAsSingleton() {
        assertSame(injector.getInstance(ConnectionHandlerFactory.class),
                injector.getInstance(ConnectionHandlerFactory.class));
    }

    @Test
    public void configure_bindsServer() {
        assertNotNull(injector.getInstance(Server.class));
    }

    @Test
    public void configure_bindsServerAsSingleton() {
        assertSame(injector.getInstance(Server.class), injector.getInstance(Server.class));
    }

}
