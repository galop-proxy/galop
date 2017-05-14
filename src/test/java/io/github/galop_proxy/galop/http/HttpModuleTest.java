package io.github.galop_proxy.galop.http;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.galop_proxy.galop.commons.CommonsModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link HttpModule}.
 */
public class HttpModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new CommonsModule(), new HttpModule());
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

}
