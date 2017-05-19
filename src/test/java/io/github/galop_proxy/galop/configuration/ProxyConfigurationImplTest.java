package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link ProxyConfigurationImpl}.
 */
public class ProxyConfigurationImplTest {

    private PortNumber port;
    private int backlogSize;
    private InetAddress bindAddress;
    private ProxyConfiguration configuration;

    @Before
    public void setUp() {
        port = new PortNumber(8080);
        backlogSize = 50;
        bindAddress = mock(InetAddress.class);
        configuration = new ProxyConfigurationImpl(port, backlogSize, bindAddress);
    }

    @Test
    public void getPort_returnsConfiguredPortNumber() {
        assertEquals(port, configuration.getPort());
    }

    @Test
    public void getBacklogSize_returnsConfiguredBacklogSize() {
        assertEquals(backlogSize, configuration.getBacklogSize());
    }

    @Test
    public void getBindAddress_returnsConfiguredInetAddress() {
        assertEquals(bindAddress, configuration.getBindAddress());
    }

    @Test
    public void getBindAddress_whenNoBindAddressIsConfigured_returnsNull() {
        configuration = new ProxyConfigurationImpl(port, backlogSize, null);
        assertNull(configuration.getBindAddress());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutPort_throwsNullPointerException() {
        new ProxyConfigurationImpl(null, backlogSize, null);
    }

}
