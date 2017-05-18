package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.PROXY_PORT;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link ProxyConfigurationFactoryImpl}.
 */
public class ProxyConfigurationFactoryImplTest {

    private ProxyConfigurationFactory factory;
    private Map<String, String> properties;

    @Before
    public void setUp() {
        factory = new ProxyConfigurationFactoryImpl();
        properties = new HashMap<>();
        properties.put(PROXY_PORT, "8080");
    }

    @Test
    public void parse_withValidProperties_returnsProxyConfiguration() throws InvalidConfigurationException {
        final ProxyConfiguration configuration = factory.parse(properties);
        assertEquals(new PortNumber(8080), configuration.getPort());
    }

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

}
