package io.github.sebastianschmidt.galop.administration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.sebastianschmidt.galop.commons.CommonsModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link AdministrationModule}.
 */
public class AdministrationModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new CommonsModule(), new AdministrationModule());
    }

    @Test
    public void configure_bindsMonitorFactory() {
        assertNotNull(injector.getInstance(MonitorFactory.class));
    }

    @Test
    public void configure_bindsMonitorFactoryAsSingleton() {
        assertSame(injector.getInstance(MonitorFactory.class),
                injector.getInstance(MonitorFactory.class));
    }

    @Test
    public void configure_bindsShutdownHandlerFactory() {
        assertNotNull(injector.getInstance(ShutdownHandlerFactory.class));
    }

    @Test
    public void configure_bindsShutdownHandlerFactoryAsSingleton() {
        assertSame(injector.getInstance(ShutdownHandlerFactory.class),
                injector.getInstance(ShutdownHandlerFactory.class));
    }

}
