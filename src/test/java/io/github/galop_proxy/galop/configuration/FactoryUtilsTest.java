package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.network.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseMaxSize;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parsePortNumber;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseTimeout;
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
    public void parseTimeout_withInvalidTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "invalid");
        parseTimeout(properties, "property", 456);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseTimeout_withNegativeTimeout_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "-1");
        parseTimeout(properties, "property", 456);
    }

    // parseMaxSize:

    @Test
    public void parseMaxSize_withValidMaxSize_returnsMaxSize() throws InvalidConfigurationException {
        properties.put("property", "255");
        assertEquals(255, parseMaxSize(properties, "property", 2048));
    }

    @Test
    public void parseMaxSize_withoutMaxSize_returnsDefaultValue() throws InvalidConfigurationException {
        assertEquals(2048, parseMaxSize(properties, "property", 2048));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseMaxSize_withInvalidMaxSize_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "invalid");
        parseMaxSize(properties, "property", 2048);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parseMaxSize_withTooLowMaxSize_throwsInvalidConfigurationException() throws InvalidConfigurationException {
        properties.put("property", "254");
        parseMaxSize(properties, "property", 2048);
    }

    // Other:

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<FactoryUtils> constructor = FactoryUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
