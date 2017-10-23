package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.network.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.FactoryUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link FactoryUtils}.
 */
public class FactoryUtilsTest {

    private Map<String, String> properties;

    @Before
    public void setUp() {
        properties = new HashMap<>();
    }

    // parsePortNumber:

    @Test
    public void parsePortNumber_withValidPort_returnsPortNumber() throws InvalidConfigurationException {
        properties.put("property", "8080");
        assertEquals(new PortNumber(8080), parsePortNumber(properties, "property"));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parsePortNumber_withMissingProperty_throwsInvalidConfigurationException()
            throws InvalidConfigurationException {
        parsePortNumber(properties, "property");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parsePortNumber_withInvalidPort_throwsInvalidConfigurationException()
            throws InvalidConfigurationException {
        properties.put("property", "invalid");
        parsePortNumber(properties, "property");
    }

    // parseTimeout:

    @Test
    public void parseTimeout_withValidTimeout_returnsTimeout() throws InvalidConfigurationException {
        properties.put("property", "123");
        assertEquals(123, parseTimeout(properties, "property", 456));
    }

    @Test
    public void parseTimeout_withoutTimeout_returnsDefaultValue() throws InvalidConfigurationException {
        assertEquals(456, parseTimeout(properties, "property", 456));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseTimeout_withInvalidInteger_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "invalid");
        parseTimeout(properties, "property", 456);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseTimeout_withNegativeTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "-1");
        parseTimeout(properties, "property", 456);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseTimeout_withTooHighTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "2147483648");
        parseTimeout(properties, "property", 456);
    }

    // parseSizeLimit:

    @Test
    public void parseSizeLimit_withValidSizeLimit_returnsSizeLimit() throws InvalidConfigurationException {
        properties.put("property", "2048");
        assertEquals(2048, parseSizeLimit(properties, "property", 8192));
    }

    @Test
    public void parseSizeLimit_withoutSizeLimit_returnsDefaultValue() throws InvalidConfigurationException {
        assertEquals(8192, parseSizeLimit(properties, "property", 8192));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseSizeLimit_withInvalidInteger_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "invalid");
        parseSizeLimit(properties, "property", 100);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseSizeLimit_withTooLowInteger_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "63");
        parseSizeLimit(properties, "property", 100);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseSizeLimit_withTooLargeInteger_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "65537");
        parseSizeLimit(properties, "property", 100);
    }

    // parseFieldsLimit:

    @Test
    public void parseFieldsLimit_withValidFieldsLimit_returnsFieldsLimit() throws InvalidConfigurationException {
        properties.put("property", "64");
        assertEquals(64, parseFieldsLimit(properties, "property", 100));
    }

    @Test
    public void parseFieldsLimit_withoutFieldsLimit_returnsDefaultValue() throws InvalidConfigurationException {
        assertEquals(100, parseFieldsLimit(properties, "property", 100));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseFieldsLimit_withInvalidInteger_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "invalid");
        parseFieldsLimit(properties, "property", 100);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseFieldsLimit_withTooLowInteger_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "0");
        parseFieldsLimit(properties, "property", 100);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseFieldsLimit_withTooLargeInteger_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "65537");
        parseFieldsLimit(properties, "property", 100);
    }

    // Other:

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<FactoryUtils> constructor = FactoryUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
