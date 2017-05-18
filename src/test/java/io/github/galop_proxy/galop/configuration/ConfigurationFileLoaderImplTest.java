package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.InetAddressFactory;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.galop_proxy.galop.configuration.ConfigurationDefaults.*;
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
    public void load_withValidFileWithAllOverwrittenDefaults_returnsSpecifiedConfiguration() throws Exception {

        final Configuration configuration = load("configuration-with-all-overwritten-defaults.properties");

        assertEquals(80, configuration.getProxyPort().getValue());
        assertEquals("localhost", configuration.getTargetAddress().getHostName());
        assertEquals(8080, configuration.getTargetPort().getValue());
        assertEquals(20000, configuration.getTargetConnectionTimeout());
        assertEquals(30000, configuration.getHttpConnectionLogInterval());
        assertEquals(15000, configuration.getHttpConnectionTerminationTimeout());
        assertEquals(45000, configuration.getHttpHeaderRequestReceiveTimeout());
        assertEquals(120000, configuration.getHttpHeaderResponseReceiveTimeout());
        assertEquals(255, configuration.getMaxHttpHeaderSize());

    }

    @Test
    public void load_withValidFileWithoutOverwrittenDefaults_returnsConfigurationWithDefaultMaxHttpHeaderSize()
            throws Exception {

        final Configuration configuration = load("configuration-without-overwritten-defaults.properties");

        assertEquals(80, configuration.getProxyPort().getValue());
        assertEquals("localhost", configuration.getTargetAddress().getHostName());
        assertEquals(8080, configuration.getTargetPort().getValue());
        assertEquals(TARGET_CONNECTION_TIMEOUT, configuration.getTargetConnectionTimeout());
        assertEquals(HTTP_CONNECTION_LOG_INTERVAL, configuration.getHttpConnectionLogInterval());
        assertEquals(HTTP_CONNECTION_TERMINATION_TIMEOUT, configuration.getHttpConnectionTerminationTimeout());
        assertEquals(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, configuration.getHttpHeaderRequestReceiveTimeout());
        assertEquals(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, configuration.getHttpHeaderResponseReceiveTimeout());
        assertEquals(HTTP_HEADER_MAX_SIZE, configuration.getMaxHttpHeaderSize());

    }

    // Invalid configuration files:

    @Test(expected = InvalidConfigurationException.class)
    public void load_withoutProxyPort_throwsInvalidConfigurationException() throws Exception {
        load("configuration-without-proxy-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidProxyPort_throwsInvalidConfigurationException() throws Exception {
        load("configuration-with-invalid-proxy-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withoutTargetAddress_throwsInvalidConfigurationException() throws Exception {
        load("configuration-without-target-address.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withUnknownTargetAddress_throwsInvalidConfigurationException() throws Exception {
        load("configuration-with-unknown-target-address.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withoutTargetPort_throwsInvalidConfigurationException() throws Exception {
        load("configuration-without-target-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidTargetPort_throwsInvalidConfigurationException() throws Exception {
        load("configuration-with-invalid-target-port.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidTargetConnectionTimeout_throwsInvalidConfigurationException() throws Exception {
        load("configuration-with-invalid-target-connection-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withTooLowTargetConnectionTimeout_throwsInvalidConfigurationException() throws Exception {
        load("configuration-with-too-low-target-connection-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidConnectionHandlersLogInterval_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-invalid-log-interval.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withTooLowConnectionHandlersLogInterval_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-too-low-log-interval.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidConnectionHandlersTerminationTimeout_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-invalid-termination-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withTooLowConnectionHandlersTerminationTimeout_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-too-low-termination-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidHttpRequestHeaderReceiveTimeout_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-invalid-http-request-header-receive-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withTooLowHttpRequestHeaderReceiveTimeout_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-too-low-http-request-header-receive-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidHttpResponseHeaderReceiveTimeout_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-invalid-http-response-header-receive-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withTooLowHttpResponseHeaderReceiveTimeout_throwsInvalidConfigurationException()
            throws Exception {
        load("configuration-with-too-low-http-response-header-receive-timeout.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withInvalidMaxHttpHeaderSize_throwsInvalidConfigurationException() throws Exception {
        load("configuration-with-invalid-max-http-header-size.properties");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void load_withTooLowMaxHttpHeaderSize_throwsInvalidConfigurationException() throws Exception {
        load("configuration-with-too-low-max-http-header-size.properties");
    }

    // Helper methods:

    private Configuration load(final String name) throws Exception {
        return loader.load(getConfigurationPath(name));
    }

    private Path getConfigurationPath(final String name) {
        try {
            return Paths.get(getClass().getResource("/io/github/galop_proxy/galop/configuration/" + name).toURI());
        } catch (final URISyntaxException ex) {
            fail(ex.getMessage());
            return null;
        }
    }

}
