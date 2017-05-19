package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link ProxyConfigurationImpl}.
 */
public class ProxyConfigurationImplTest {

    private PortNumber port;
    private int backlogSize;
    private ProxyConfiguration configuration;

    @Before
    public void setUp() {
        port = new PortNumber(8080);
        backlogSize = 50;
        configuration = new ProxyConfigurationImpl(port, backlogSize);
    }

    @Test
    public void getPort_returnsConfiguredPortNumber() {
        assertEquals(port, configuration.getPort());
    }

    @Test
    public void getBacklogSize_returnsConfiguredBacklogSize() {
        assertEquals(backlogSize, configuration.getBacklogSize());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutPort_throwsNullPointerException() {
        new ProxyConfigurationImpl(null, backlogSize);
    }

}
