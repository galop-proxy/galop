package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.network.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link TargetConfigurationImpl}.
 */
public class TargetConfigurationImplTest {

    private InetAddress address;
    private PortNumber port;
    private int connectionTimeout;
    private TargetConfiguration configuration;

    @Before
    public void setUp() {
        address = mock(InetAddress.class);
        port = new PortNumber(80);
        connectionTimeout = 10000;
        configuration = new TargetConfigurationImpl(address, port, connectionTimeout);
    }

    @Test
    public void getAddress_returnsConfiguredAddress() {
        assertEquals(address, configuration.getAddress());
    }

    @Test
    public void getPort_returnsConfiguredPort() {
        assertEquals(port, configuration.getPort());
    }

    @Test
    public void getConnectionTimeout_returnsConfiguredConnectionTimeout() {
        assertEquals(connectionTimeout, configuration.getConnectionTimeout());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutAddress_throwsNullPointerException() {
        new TargetConfigurationImpl(null, port, connectionTimeout);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutPort_throwsNullPointerException() {
        new TargetConfigurationImpl(address, null, connectionTimeout);
    }

}
