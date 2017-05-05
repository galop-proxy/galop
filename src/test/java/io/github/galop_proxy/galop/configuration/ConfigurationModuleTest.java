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

}
