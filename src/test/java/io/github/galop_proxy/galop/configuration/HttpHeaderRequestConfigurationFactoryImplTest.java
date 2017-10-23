package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.*;
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
        properties.put(HTTP_HEADER_REQUEST_REQUEST_LINE_SIZE_LIMIT, "4096");
        properties.put(HTTP_HEADER_REQUEST_FIELDS_LIMIT, "64");
        configuration = factory.parse(properties);
    }

    @Test
    public void parse_withValidReceiveTimeout_returnsConfiguredValue() {
        assertEquals(45000, configuration.getReceiveTimeout());
    }

    @Test
    public void parse_withoutReceiveTimeout_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, configuration.getReceiveTimeout());
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withTooHighReceiveTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, "2147483648");
        factory.parse(properties);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parse_withNegativeReceiveTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, "-1");
        factory.parse(properties);
    }

    @Test
    public void parse_withValidRequestLineSizeLimit_returnsConfiguredValue() {
        assertEquals(4096, configuration.getRequestLineSizeLimit());
    }

    @Test
    public void parse_withoutRequestLineSizeLimit_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_REQUEST_REQUEST_LINE_SIZE_LIMIT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_REQUEST_REQUEST_LINE_SIZE_LIMIT, configuration.getRequestLineSizeLimit());
    }

    @Test
    public void parse_withValidFieldsLimit_returnsConfiguredValue() {
        assertEquals(64, configuration.getFieldsLimit());
    }

    @Test
    public void parse_withoutFieldsLimit_returnsDefaultValue() throws InvalidConfigurationException {
        properties.remove(HTTP_HEADER_REQUEST_FIELDS_LIMIT);
        configuration = factory.parse(properties);
        assertEquals(ConfigurationDefaults.HTTP_HEADER_REQUEST_FIELDS_LIMIT, configuration.getFieldsLimit());
    }
    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }


}
