package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_REQUEST_MAX_SIZE;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link HttpHeaderRequestConfigurationFactoryImpl}.
 */
public class HttpHeaderRequestConfigurationFactoryImplTest {

    private HttpHeaderRequestConfigurationFactory factory;
    private Map<String, String> properties;
    private HttpHeaderRequestConfiguration configuration;

    @Before
    public void setUp() throws InvalidConfigurationException {
        factory = new HttpHeaderRequestConfigurationFactoryImpl();
        properties = new HashMap<>();
        properties.put(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, "45000");
        properties.put(HTTP_HEADER_REQUEST_MAX_SIZE, "2048");
        configuration = factory.parse(properties);
    }

    // Valid configuration:

    @Test
    public void parse_withValidReceiveTimeout_returnsConfiguration() {
        assertEquals(45000, configuration.getReceiveTimeout());
    }

    @Test
    public void parse_withoutReceiveTimeout_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, configuration.getReceiveTimeout());
    }

    @Test
    public void parse_withValidMaxSize_returnsConfiguration() {
        assertEquals(2048, configuration.getMaxSize());
    }

    @Test
    public void parse_withoutMaxSize_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_REQUEST_MAX_SIZE);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_REQUEST_MAX_SIZE, configuration.getMaxSize());
    }

    // Invalid configuration:

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidReceiveTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withNegativeReceiveTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, "-1");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidMaxSize_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_REQUEST_MAX_SIZE, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withMaxSizeSmallerThan255_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_REQUEST_MAX_SIZE, "254");
        factory.parse(properties);
    }

    // Other:

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }


}
