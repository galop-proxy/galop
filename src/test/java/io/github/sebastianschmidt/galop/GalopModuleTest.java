package io.github.sebastianschmidt.galop;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.sebastianschmidt.galop.commons.ServerSocketFactory;
import io.github.sebastianschmidt.galop.parser.HttpHeaderParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link GalopModule}.
 */
public class GalopModuleTest {

    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new GalopModule());
    }

    @Test
    public void configure_installsCommonsModule() {
        assertNotNull(injector.getInstance(ServerSocketFactory.class));
    }

    @Test
    public void configure_installsParserModule() {
        assertNotNull(injector.getInstance(HttpHeaderParser.class));
    }

}
