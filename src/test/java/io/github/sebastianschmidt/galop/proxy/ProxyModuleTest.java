package io.github.sebastianschmidt.galop.proxy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.sebastianschmidt.galop.commons.CommonsModule;
import io.github.sebastianschmidt.galop.parser.ParserModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link ProxyModule}.
 */
public class ProxyModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new CommonsModule(), new ParserModule(), new ProxyModule());
    }

    @Test
    public void configure_bindsServerFactory() {
        assertNotNull(injector.getInstance(ServerFactory.class));
    }

    @Test
    public void configure_bindsServerFactoryAsSingleton() {
        assertSame(injector.getInstance(ServerFactory.class),
                injector.getInstance(ServerFactory.class));
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

}
