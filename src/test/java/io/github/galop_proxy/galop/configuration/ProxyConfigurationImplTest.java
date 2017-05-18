package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.PROXY_PORT;
import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link ProxyConfigurationImpl}.
 */
public class ProxyConfigurationImplTest {

    private PortNumber port;
    private ProxyConfiguration configuration;

    @Before
    public void setUp() {
        port = new PortNumber(8080);
        configuration = new ProxyConfigurationImpl(port);
    }

    @Test
    public void getPort_returnsConfiguredPortNumber() {
        assertEquals(port, configuration.getPort());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutPort_throwsNullPointerException() {
        new ProxyConfigurationImpl(null);
    }

}
