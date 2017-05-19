package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.PROXY_BACKLOG_SIZE;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.PROXY_PORT;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link ProxyConfigurationFactoryImpl}.
 */
public class ProxyConfigurationFactoryImplTest {

    private ProxyConfigurationFactory factory;
    private Map<String, String> properties;
    private ProxyConfiguration configuration;

    @Before
    public void setUp() throws InvalidConfigurationException {

        factory = new ProxyConfigurationFactoryImpl();

        properties = new HashMap<>();
        properties.put(PROXY_PORT, "8080");
        properties.put(PROXY_BACKLOG_SIZE, "100");

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

}
