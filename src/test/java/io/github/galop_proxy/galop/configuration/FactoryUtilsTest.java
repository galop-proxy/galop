package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.FactoryUtils.parsePortNumber;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link FactoryUtils}.
 */
public class FactoryUtilsTest {

    @Test
    public void parsePortNumber_withValidPort_returnsPortNumber() throws InvalidConfigurationException {
        final Map<String, String> properties = new HashMap<>();
        properties.put("property", "8080");
        assertEquals(new PortNumber(8080), parsePortNumber(properties, "property"));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parsePortNumber_withMissingProperty_throwsInvalidConfigurationException()
            throws InvalidConfigurationException {
        final Map<String, String> properties = new HashMap<>();
        parsePortNumber(properties, "property");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void parsePortNumber_withInvalidPort_throwsInvalidConfigurationException()
            throws InvalidConfigurationException {
        final Map<String, String> properties = new HashMap<>();
        properties.put("property", "invalid");
        parsePortNumber(properties, "property");
    }

    @Test(expected = Exception.class)
    public void constructor_throwsException() throws Exception {
        final Constructor<FactoryUtils> constructor = FactoryUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
