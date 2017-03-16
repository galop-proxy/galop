package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.InetAddressFactory;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link ConfigurationFileLoaderImpl}.
 */
public class ConfigurationFileLoaderImplTest {

    private ConfigurationFileLoaderImpl loader;

    @Before
    public void setUp() throws UnknownHostException {

        final InetAddressFactory inetAddressFactory = mock(InetAddressFactory.class);
        final InetAddress inetAddress = mock(InetAddress.class);
        when(inetAddress.getHostName()).thenReturn("localhost");
        when(inetAddressFactory.createByName("localhost")).thenReturn(inetAddress);
        doThrow(UnknownHostException.class).when(inetAddressFactory).createByName("unknown");

        loader = new ConfigurationFileLoaderImpl(inetAddressFactory);

    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutInetAddressFactory_throwsNullPointerException() {
        new ConfigurationFileLoaderImpl(null);
    }

    // Valid configuration files:

    @Test
    public void load_withValidFileWithMaxHttpHeaderSize_returnsSpecifiedConfiguration() throws Exception {

        final Path path = getConfigurationPath("valid-configuration-with-max-http-header-size.properties");
        final Configuration configuration = loader.load(path);

        assertEquals(80, configuration.getProxyPort().getValue());
        assertEquals("localhost", configuration.getTargetAddress().getHostName());
        assertEquals(8080, configuration.getTargetPort().getValue());
        assertEquals(16384, configuration.getMaxHttpHeaderSize());

    }

    @Test
    public void load_withValidFileWithoutMaxHttpHeaderSize_returnsConfigurationWithDefaultMaxHttpHeaderSize()
            throws Exception {

        final Configuration configuration = load("valid-configuration-without-max-http-header-size.properties");

        assertEquals(80, configuration.getProxyPort().getValue());
        assertEquals("localhost", configuration.getTargetAddress().getHostName());
        assertEquals(8080, configuration.getTargetPort().getValue());
        assertEquals(ConfigurationDefaults.MAX_HTTP_HEADER_SIZE, configuration.getMaxHttpHeaderSize());

    }

    // Invalid configuration files:

    @Test(expected = InvalidConfigurationException.class)
    public void load_withoutProxyPort_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-without-proxy-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidProxyPort_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-with-invalid-proxy-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withoutTargetAddress_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-without-target-address.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withUnknownTargetAddress_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-with-unknown-target-address.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withoutTargetPort_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-without-target-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidTargetPort_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-with-invalid-target-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidMaxHttpHeaderSize_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-with-invalid-max-http-header-size.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withTooLowMaxHttpHeaderSize_throwsInvalidConfigurationException() throws Exception {
        load("invalid-configuration-with-too-low-max-http-header-size.properties");
    }

    // Helper methods:

    private Configuration load(final String name) throws Exception {
        return loader.load(getConfigurationPath(name));
    }

    private Path getConfigurationPath(final String name) {
        try {
            return Paths.get(getClass().getResource("/io/github/sebastianschmidt/galop/configuration/" + name).toURI());
        } catch (final URISyntaxException ex) {
            fail(ex.getMessage());
            return null;
        }
    }

}
