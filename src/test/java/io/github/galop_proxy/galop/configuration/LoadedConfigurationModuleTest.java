package io.github.galop_proxy.galop.configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.AbstractConfigurationTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link LoadedConfigurationModule}.
 */
public class LoadedConfigurationModuleTest extends AbstractConfigurationTest {

    private Injector injector;

    @Before
    public void setUp() {
        super.setUp();
        injector = Guice.createInjector(new LoadedConfigurationModule(configuration));
    }

    @Test
    public void configure_bindsConfiguration() {
        assertNotNull(injector.getInstance(Configuration.class));
    }

    @Test
    public void configure_bindsProxyConfiguration() {
        assertNotNull(injector.getInstance(ProxyConfiguration.class));
    }

    @Test
    public void configure_bindsTargetConfiguration() {
        assertNotNull(injector.getInstance(TargetConfiguration.class));
    }

    @Test
    public void configure_bindsHttpConfiguration() {
        assertNotNull(injector.getInstance(HttpConfiguration.class));
    }

    @Test
    public void configure_bindsHttpConnectionConfiguration() {
        assertNotNull(injector.getInstance(HttpConnectionConfiguration.class));
    }

    @Test
    public void configure_bindsHttpHeaderConfiguration() {
        assertNotNull(injector.getInstance(HttpHeaderConfiguration.class));
    }

    @Test
    public void configure_bindsHttpHeaderRequestConfiguration() {
        assertNotNull(injector.getInstance(HttpHeaderRequestConfiguration.class));
    }

    @Test
    public void configure_bindsHttpHeaderResponseConfiguration() {
        assertNotNull(injector.getInstance(HttpHeaderResponseConfiguration.class));
    }

}
