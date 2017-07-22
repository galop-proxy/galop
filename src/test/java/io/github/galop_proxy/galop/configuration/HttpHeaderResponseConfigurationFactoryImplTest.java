package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_RESPONSE_FIELDS_LIMIT;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_RESPONSE_MAX_SIZE;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link HttpHeaderResponseConfigurationFactoryImpl}.
 */
public class HttpHeaderResponseConfigurationFactoryImplTest {

    private HttpHeaderResponseConfigurationFactory factory;
    private Map<String, String> properties;
    private HttpHeaderResponseConfiguration configuration;

    @Before
    public void setUp() throws InvalidConfigurationException {
        factory = new HttpHeaderResponseConfigurationFactoryImpl();
        properties = new HashMap<>();
        properties.put(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, "120000");
        properties.put(HTTP_HEADER_RESPONSE_FIELDS_LIMIT, "64");
        properties.put(HTTP_HEADER_RESPONSE_MAX_SIZE, "4084");
        configuration = factory.parse(properties);
    }

    // Valid configuration:

    @Test
    public void parse_withValidReceiveTimeout_returnsConfiguration() {
        assertEquals(120000, configuration.getReceiveTimeout());
    }

    @Test
    public void parse_withoutReceiveTimeout_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, configuration.getReceiveTimeout());
    }

    @Test
    public void parse_withValidFieldsLimit_returnsConfiguration() {
        assertEquals(64, configuration.getFieldsLimit());
    }

    @Test
    public void parse_withoutFieldsLimit_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_RESPONSE_FIELDS_LIMIT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_REQUEST_FIELDS_LIMIT, configuration.getFieldsLimit());
    }

    @Test
    public void parse_withValidMaxSize_returnsConfiguration() {
        assertEquals(4084, configuration.getMaxSize());
    }

    @Test
    public void parse_withoutMaxSize_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_RESPONSE_MAX_SIZE);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_RESPONSE_MAX_SIZE, configuration.getMaxSize());
    }

    // Invalid configuration:

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidReceiveTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withNegativeReceiveTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, "-1");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidFieldsLimit_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_RESPONSE_FIELDS_LIMIT, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withTooLowFieldsLimit_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_RESPONSE_FIELDS_LIMIT, "0");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withTooLargeFieldsLimit_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_RESPONSE_FIELDS_LIMIT, "65537");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withInvalidMaxSize_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_RESPONSE_MAX_SIZE, "invalid");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withMaxSizeSmallerThan255_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_RESPONSE_MAX_SIZE, "254");
        factory.parse(properties);
    }

    // Other:

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }


}
