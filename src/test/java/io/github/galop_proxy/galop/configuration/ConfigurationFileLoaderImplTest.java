package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.AbstractConfigurationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ConfigurationFileLoaderImpl}.
 */
public class ConfigurationFileLoaderImplTest extends AbstractConfigurationTest {

    private ConfigurationFactory factory;
    private ConfigurationFileLoader loader;

    @Before
    public void setUp() {
        super.setUp();
        factory = mock(ConfigurationFactory.class);
        loader = new ConfigurationFileLoaderImpl(factory);
    }

    // load:

    @Test
    public void load_withValidConfiguration_returnsLoadedConfiguration() throws Exception {

        final Map<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put("proxy.port", "80");
        expectedProperties.put("target.address", "localhost");
        expectedProperties.put("target.port", "8080");
        when(factory.parse(eq(expectedProperties))).thenReturn(configuration);

        assertSame(configuration, loader.load(getConfigurationPath("configuration.properties")));
        verify(factory).parse(eq(expectedProperties));

    }

    @Test(expected = NullPointerException.class)
    public void load_withoutPath_throwsNullPointerException() throws Exception {
        loader.load(null);
    }

    @Test(expected = IOException.class)
    public void load_withNotExistingFile_throwsIOException() throws Exception {
        loader.load(Paths.get("not-existing-file"));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidConfiguration_throwsInvalidConfigurationException() throws Exception {
        doThrow(InvalidConfigurationException.class).when(factory).parse(any());
        loader.load(getConfigurationPath("invalid-configuration.properties"));
    }

    // constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfigurationFileLoader_throwsNullPointerException() {
        new ConfigurationFileLoaderImpl(null);
    }

    // Helper method:

    private Path getConfigurationPath(final String name) {
        try {
            return Paths.get(getClass().getResource("/io/github/galop_proxy/galop/configuration/" + name).toURI());
        } catch (final URISyntaxException ex) {
            fail(ex.getMessage());
            return null;
        }
    }

}
