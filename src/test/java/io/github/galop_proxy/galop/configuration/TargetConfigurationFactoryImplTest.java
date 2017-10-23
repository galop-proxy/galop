package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.api.network.InetAddressFactory;
import io.github.galop_proxy.galop.network.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.TARGET_ADDRESS;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.TARGET_CONNECTION_TIMEOUT;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.TARGET_PORT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link TargetConfigurationFactoryImpl}.
 */
public class TargetConfigurationFactoryImplTest {

    private TargetConfigurationFactory factory;
    private Map<String, String> properties;
    private TargetConfiguration configuration;

    @Before
    public void setUp() throws Exception {

        final InetAddressFactory inetAddressFactory = mock(InetAddressFactory.class);
        final InetAddress inetAddress = mock(InetAddress.class);
        when(inetAddress.getHostName()).thenReturn("localhost");
        when(inetAddressFactory.createByName("localhost")).thenReturn(inetAddress);
        doThrow(UnknownHostException.class).when(inetAddressFactory).createByName("unknown");

        factory = new TargetConfigurationFactoryImpl(inetAddressFactory);

        properties = new HashMap<>();
        properties.put(TARGET_ADDRESS, "localhost");
        properties.put(TARGET_PORT, "80");
        properties.put(TARGET_CONNECTION_TIMEOUT, "10000");

        configuration = factory.parse(properties);

    }

    // Valid configuration:

    @Test
    public void parse_withValidAddress_returnsConfiguration() {
        assertEquals("localhost", configuration.getAddress().getHostName());
    }

    @Test
    public void parse_withValidPort_returnsConfiguration() {
        assertEquals(new PortNumber(80), configuration.getPort());
    }

    @Test
    public void parse_withValidConnectionTimeout_returnsConfiguration() {
        assertEquals(10000, configuration.getConnectionTimeout());
    }

    @Test
    public void parse_withoutConnectionTimeout_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(TARGET_CONNECTION_TIMEOUT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.TARGET_CONNECTION_TIMEOUT, configuration.getConnectionTimeout());
    }

    // Invalid configuration:

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withoutAddress_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.remove(TARGET_ADDRESS);
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withUnknownAddress_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(TARGET_ADDRESS, "unknown");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withoutPort_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.remove(TARGET_PORT);
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidPort_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(TARGET_PORT, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidConnectionTimeout_throwsInvalidConfigurationException()
            throws InvalidConfigurationException {
        properties.put(TARGET_CONNECTION_TIMEOUT, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withNegativeConnectionTimeout_throwsInvalidConfigurationException()
            throws InvalidConfigurationException {
        properties.put(TARGET_CONNECTION_TIMEOUT, "-1");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withTooHighConnectionTimeout_throwsInvalidConfigurationException()
            throws InvalidConfigurationException {
        properties.put(TARGET_CONNECTION_TIMEOUT, "2147483648");
        factory.parse(properties);
    }

    // Other:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutInetAddressFactory_throwsNullPointerException() {
        new TargetConfigurationFactoryImpl(null);
    }

}
