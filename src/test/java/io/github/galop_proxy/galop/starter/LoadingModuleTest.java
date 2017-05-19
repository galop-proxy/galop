package io.github.galop_proxy.galop.starter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.configuration.ConfigurationFileLoader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link LoadingModule}.
 */
public class LoadingModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new LoadingModule());
    }

    @Test
    public void configure_bindsCommonsModule() {
        assertNotNull(injector.getInstance(Runtime.class));
    }

    @Test
    public void configure_bindsConfigurationModule() {
        assertNotNull(injector.getInstance(ConfigurationFileLoader.class));
    }

    @Test
    public void configure_bindsLoader() {
        assertNotNull(injector.getInstance(Loader.class));
    }

    @Test
    public void configure_bindsLoaderAsSingleton() {
        assertSame(injector.getInstance(Loader.class), injector.getInstance(Loader.class));
    }

}
