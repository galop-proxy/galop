package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_CONNECTION_LOG_INTERVAL;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_CONNECTION_TERMINATION_TIMEOUT;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link HttpConnectionConfigurationFactoryImpl}.
 */
public class HttpConnectionConfigurationFactoryImplTest {

    private HttpConnectionConfigurationFactory factory;
    private Map<String, String> properties;
    private HttpConnectionConfiguration configuration;

    @Before
    public void setupUp() throws InvalidConfigurationException {
        factory = new HttpConnectionConfigurationFactoryImpl();
        properties = new HashMap<>();
        properties.put(HTTP_CONNECTION_LOG_INTERVAL, "45000");
        properties.put(HTTP_CONNECTION_TERMINATION_TIMEOUT, "15000");
        configuration = factory.parse(properties);
    }

    // Valid configuration:

    @Test
    public void parse_withValidLogInterval_returnsConfiguration() {
        assertEquals(45000, configuration.getLogInterval());
    }

    @Test
    public void parse_withoutLogInterval_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_CONNECTION_LOG_INTERVAL);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_CONNECTION_LOG_INTERVAL, configuration.getLogInterval());
    }

    @Test
    public void parse_withValidTerminationTimeout_returnsConfiguration() {
        assertEquals(15000, configuration.getTerminationTimeout());
    }

    @Test
    public void parse_withoutTerminationTimeout_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_CONNECTION_TERMINATION_TIMEOUT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_CONNECTION_TERMINATION_TIMEOUT, configuration.getTerminationTimeout());
    }

    // Invalid configuration:

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidLogInterval_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_CONNECTION_LOG_INTERVAL, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withNegativeLogInterval_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_CONNECTION_LOG_INTERVAL, "-1");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidTerminationTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_CONNECTION_TERMINATION_TIMEOUT, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withNegativeTerminationTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_CONNECTION_TERMINATION_TIMEOUT, "-1");
        factory.parse(properties);
    }

    // Other:

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }

}
