package io.github.sebastianschmidt.galop.commons;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link CommonsModule}.
 */
public class CommonsModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new CommonsModule());
    }

    @Test
    public void configure_bindsSocketFactory() {
        assertNotNull(injector.getInstance(SocketFactory.class));
    }

    @Test
    public void configure_bindsServerSocketFactory() {
        assertNotNull(injector.getInstance(ServerSocketFactory.class));
    }

    @Test
    public void configure_bindsInetAddressFactory() {
        assertNotNull(injector.getInstance(InetAddressFactory.class));
    }

}
