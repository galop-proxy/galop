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

    // Handler:

    @Test
    public void configure_bindsExchangeHandler() {
        assertNotNull(injector.getInstance(ExchangeHandler.class));
    }

    @Test
    public void configure_bindsExchangeHandlerAsSingleton() {
        assertSame(injector.getInstance(ExchangeHandler.class), injector.getInstance(ExchangeHandler.class));
    }

    // Parser:

    @Test
    public void configure_bindsMessageParser() {
        assertNotNull(injector.getInstance(MessageParser.class));
    }

    @Test
    public void configure_bindsMessageParserAsSingleton() {
        assertSame(injector.getInstance(MessageParser.class), injector.getInstance(MessageParser.class));
    }

    @Test
    public void configure_bindsStartLineParser() {
        assertNotNull(injector.getInstance(StartLineParser.class));
    }

    @Test
    public void configure_bindsStartLineParserAsSingleton() {
        assertSame(injector.getInstance(StartLineParser.class), injector.getInstance(StartLineParser.class));
    }

    @Test
    public void configure_bindsHeaderParser() {
        assertNotNull(injector.getInstance(HeaderParser.class));
    }

    @Test
    public void configure_bindsHeaderParserAsSingleton() {
        assertSame(injector.getInstance(HeaderParser.class), injector.getInstance(HeaderParser.class));
    }

    // Writer:

    @Test
    public void configure_bindsMessageWriter() {
        assertNotNull(injector.getInstance(MessageWriter.class));
    }

    @Test
    public void configure_bindsMessageWriterAsSingleton() {
        assertSame(injector.getInstance(MessageWriter.class), injector.getInstance(MessageWriter.class));
    }

    @Test
    public void configure_bindsStartLineWriter() {
        assertNotNull(injector.getInstance(StartLineWriter.class));
    }

    @Test
    public void configure_bindsStartLineWriterAsSingleton() {
        assertSame(injector.getInstance(StartLineWriter.class), injector.getInstance(StartLineWriter.class));
    }

    // Old:

    @Test
    public void configure_bindsHttpHeaderParser() {
        assertNotNull(injector.getInstance(HttpHeaderParser.class));
    }

    @Test
    public void configure_bindsHttpHeaderParserAsSingleton() {
        assertSame(injector.getInstance(HttpHeaderParser.class), injector.getInstance(HttpHeaderParser.class));
    }

}
