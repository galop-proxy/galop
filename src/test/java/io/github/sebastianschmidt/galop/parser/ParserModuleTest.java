package io.github.sebastianschmidt.galop.parser;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the class {@link ParserModule}.
 */
public class ParserModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new ParserModule());
    }

    @Test
    public void configure_bindsHttpHeaderParser() {
        assertNotNull(injector.getInstance(HttpHeaderParser.class));
    }

    @Test
    public void configure_bindsHttpHeaderParserAsSingleton() {
        assertSame(injector.getInstance(HttpHeaderParser.class), injector.getInstance(HttpHeaderParser.class));
    }

}
