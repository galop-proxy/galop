package io.github.galop_proxy.galop.configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.commons.CommonsModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link ConfigurationModule}.
 */
public class ConfigurationModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new CommonsModule(), new ConfigurationModule());
    }

    @Test
    public void configure_bindsConfigurationFileLoader() {
        assertNotNull(injector.getInstance(ConfigurationFileLoader.class));
    }

    @Test
    public void configure_bindsConfigurationFileLoaderAsSingleton() {
        assertSame(injector.getInstance(ConfigurationFileLoader.class),
                injector.getInstance(ConfigurationFileLoader.class));
    }

    @Test
    public void configure_bindsProxyConfigurationFactory() {
        assertNotNull(injector.getInstance(ProxyConfigurationFactory.class));
    }

    @Test
    public void configure_bindsProxyConfigurationFactoryAsSingleton() {
        assertSame(injector.getInstance(ProxyConfigurationFactory.class),
                injector.getInstance(ProxyConfigurationFactory.class));
    }

    @Test
    public void configure_bindsTargetConfigurationFactory() {
        assertNotNull(injector.getInstance(TargetConfigurationFactory.class));
    }

    @Test
    public void configure_bindsTargetConfigurationFactoryAsSingleton() {
        assertSame(injector.getInstance(TargetConfigurationFactory.class),
                injector.getInstance(TargetConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpConnectionConfigurationFactory() {
        assertNotNull(injector.getInstance(HttpConnectionConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpConnectionConfigurationFactoryAsSingleton() {
        assertSame(injector.getInstance(HttpConnectionConfigurationFactory.class),
                injector.getInstance(HttpConnectionConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpHeaderRequestConfigurationFactory() {
        assertNotNull(injector.getInstance(HttpHeaderRequestConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpHeaderRequestConfigurationFactoryAsSingleton() {
        assertSame(injector.getInstance(HttpHeaderRequestConfigurationFactory.class),
                injector.getInstance(HttpHeaderRequestConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpHeaderResponseConfigurationFactory() {
        assertNotNull(injector.getInstance(HttpHeaderResponseConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpHeaderResponseConfigurationFactoryAsSingleton() {
        assertSame(injector.getInstance(HttpHeaderResponseConfigurationFactory.class),
                injector.getInstance(HttpHeaderResponseConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpHeaderConfigurationFactory() {
        assertNotNull(injector.getInstance(HttpHeaderConfigurationFactory.class));
    }

    @Test
    public void configure_bindsHttpHeaderConfigurationFactoryAsSingleton() {
        assertSame(injector.getInstance(HttpHeaderConfigurationFactory.class),
                injector.getInstance(HttpHeaderConfigurationFactory.class));
    }

}
