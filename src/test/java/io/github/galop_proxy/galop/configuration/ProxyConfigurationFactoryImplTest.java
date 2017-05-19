package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.InetAddressFactory;
import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link ProxyConfigurationFactoryImpl}.
 */
public class ProxyConfigurationFactoryImplTest {

    private InetAddress localHost;
    private ProxyConfigurationFactory factory;
    private Map<String, String> properties;
    private ProxyConfiguration configuration;

    @Before
    public void setUp() throws Exception {

        localHost = InetAddress.getLocalHost();

        final InetAddressFactory inetAddressFactory = mock(InetAddressFactory.class);
        when(inetAddressFactory.createByName(localHost.getHostName())).thenReturn(localHost);
        doThrow(UnknownHostException.class).when(inetAddressFactory).createByName("unknown");

        factory = new ProxyConfigurationFactoryImpl(inetAddressFactory);

        properties = new HashMap<>();
        properties.put(PROXY_PORT, "8080");
        properties.put(PROXY_BACKLOG_SIZE, "100");
        properties.put(PROXY_BIND_ADDRESS, localHost.getHostName());

        configuration = factory.parse(properties);

    }

    // Valid configuration:

    @Test
    public void parse_withValidPort_returnsConfiguration() throws InvalidConfigurationException {
        assertEquals(new PortNumber(8080), configuration.getPort());
    }

    @Test
    public void parse_withValidBacklogSize_returnsConfiguration() throws InvalidConfigurationException {
        assertEquals(100, configuration.getBacklogSize());
    }

    @Test
    public void parse_withoutBacklogSize_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(PROXY_BACKLOG_SIZE);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.PROXY_BACKLOG_SIZE, configuration.getBacklogSize());
    }

    @Test
    public void parse_withValidBindAddress_returnsConfiguration() throws Exception {
        assertEquals(localHost, configuration.getBindAddress());
    }

    @Test
    public void parse_withoutBindAddress_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(PROXY_BIND_ADDRESS);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.PROXY_BIND_ADDRESS, configuration.getBindAddress());
    }

    // Invalid configuration:

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withoutPort_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.remove(PROXY_PORT);
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidPort_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(PROXY_PORT, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidBacklogSize_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(PROXY_BACKLOG_SIZE, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withTooLowBacklogSize_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(PROXY_BACKLOG_SIZE, "0");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withUnknownBindAddress_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(PROXY_BIND_ADDRESS, "unknown");
        factory.parse(properties);
    }

}
