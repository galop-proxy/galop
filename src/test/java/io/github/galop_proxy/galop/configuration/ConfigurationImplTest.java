package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link ConfigurationImpl}.
 */
public class ConfigurationImplTest {

    private ConfigurationImpl configuration;

    @Before
    public void setUp() throws UnknownHostException {
        configuration = new ConfigurationImpl(
                new PortNumber(8080), InetAddress.getLocalHost(), new PortNumber(80));
    }

    @Test
    public void getProxyPort_returnsConfiguredProxyPort() {
        assertEquals(8080, configuration.getProxyPort().getValue());
    }

    @Test
    public void getTargetAddress_returnsConfiguredTargetAddress() throws UnknownHostException {
        assertEquals(InetAddress.getLocalHost(), configuration.getTargetAddress());
    }

    @Test
    public void getTargetPort_returnsConfiguredTargetPort() {
        assertEquals(80, configuration.getTargetPort().getValue());
    }

    @Test
    public void getTargetConnectionTimeout_withoutConfiguredProperty_returnsDefaultValue() {
        assertEquals(ConfigurationDefaults.TARGET_CONNECTION_TIMEOUT, configuration.getTargetConnectionTimeout());
    }

    @Test
    public void getTargetConnectionTimeout_withConfiguredProperty_returnsConfiguredValue() {
        configuration.setTargetConnectionTimeout(1000);
        assertEquals(1000, configuration.getTargetConnectionTimeout());
    }

    @Test
    public void getConnectionHandlersLogInterval_withoutConfiguredProperty_returnsDefaultValue() {
        assertEquals(ConfigurationDefaults.HTTP_CONNECTION_LOG_INTERVAL,
                configuration.getConnectionHandlersLogInterval());
    }

    @Test
    public void getConnectionHandlersLogInterval_withConfiguredProperty_returnsConfiguredValue() {
        configuration.setConnectionHandlersLogInterval(10000);
        assertEquals(10000, configuration.getConnectionHandlersLogInterval());
    }

    @Test
    public void getConnectionHandlersTerminationTimeout_withoutConfiguredProperty_returnsDefaultValue() {
        assertEquals(ConfigurationDefaults.HTTP_CONNECTION_TERMINATION_TIMEOUT,
                configuration.getConnectionHandlersTerminationTimeout());
    }

    @Test
    public void getConnectionHandlersTerminationTimeout_withConfiguredProperty_returnsConfiguredValue() {
        configuration.setConnectionHandlersTerminationTimeout(60000);
        assertEquals(60000, configuration.getConnectionHandlersTerminationTimeout());
    }

    @Test
    public void getHttpRequestHeaderReceiveTimeout_withoutConfiguredProperty_returnsDefaultValue() {
        assertEquals(ConfigurationDefaults.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT,
                configuration.getHttpRequestHeaderReceiveTimeout());
    }

    @Test
    public void getHttpRequestHeaderReceiveTimeout_withConfiguredProperty_returnsConfiguredValue() {
        configuration.setHttpRequestHeaderReceiveTimeout(30000);
        assertEquals(30000, configuration.getHttpRequestHeaderReceiveTimeout());
    }

    @Test
    public void getHttpResponseHeaderReceiveTimeout_withoutConfiguredProperty_returnsDefaultValue() {
        assertEquals(ConfigurationDefaults.HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT,
                configuration.getHttpResponseHeaderReceiveTimeout());
    }

    @Test
    public void getHttpResponseHeaderReceiveTimeout_withConfiguredProperty_returnsConfiguredValue() {
        configuration.setHttpResponseHeaderReceiveTimeout(120000);
        assertEquals(120000, configuration.getHttpResponseHeaderReceiveTimeout());
    }

    @Test
    public void getMaxHttpHeaderSize_withoutConfiguredProperty_returnsDefaultValue() {
        assertEquals(ConfigurationDefaults.HTTP_HEADER_MAX_SIZE, configuration.getMaxHttpHeaderSize());
    }

    @Test
    public void getMaxHttpHeaderSize_withConfiguredProperty_returnsConfiguredValue() {
        configuration.setMaxHttpHeaderSize(1024);
        assertEquals(1024, configuration.getMaxHttpHeaderSize());
    }

}
