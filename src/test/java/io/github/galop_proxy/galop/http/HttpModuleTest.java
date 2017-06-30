package io.github.galop_proxy.galop.http;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.AbstractConfigurationTest;
import io.github.galop_proxy.galop.network.NetworkModule;
import io.github.galop_proxy.galop.configuration.LoadedConfigurationModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link HttpModule}.
 */
public class HttpModuleTest extends AbstractConfigurationTest {

    private Injector injector;

    @Before
    public void setUp() {
        super.setUp();
        injector = Guice.createInjector(
                new NetworkModule(), new LoadedConfigurationModule(configuration), new HttpModule());
    }

    @Test
    public void configure_bindsMessageParser() {
        assertNotNull(injector.getInstance(MessageParser.class));
    }

    @Test
    public void configure_bindsMessageParserAsSingleton() {
        assertSame(injector.getInstance(MessageParser.class), injector.getInstance(MessageParser.class));
    }

    @Test
    public void configure_bindsHttpHeaderParser() {
        assertNotNull(injector.getInstance(HttpHeaderParser.class));
    }

    @Test
    public void configure_bindsHttpHeaderParserAsSingleton() {
        assertSame(injector.getInstance(HttpHeaderParser.class), injector.getInstance(HttpHeaderParser.class));
    }

    @Test
    public void configure_bindsHttpMessageHandler() {
        assertNotNull(injector.getInstance(HttpMessageHandler.class));
    }

    @Test
    public void configure_bindsHttpMessageHandlerAsSingleton() {
        assertSame(injector.getInstance(HttpMessageHandler.class), injector.getInstance(HttpMessageHandler.class));
    }

    @Test
    public void configure_bindsHttpExchangeHandler() {
        assertNotNull(injector.getInstance(HttpExchangeHandler.class));
    }

    @Test
    public void configure_bindsHttpExchangeHandlerAsSingleton() {
        assertSame(injector.getInstance(HttpExchangeHandler.class), injector.getInstance(HttpExchangeHandler.class));
    }

}
