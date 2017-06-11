package io.github.galop_proxy.galop.network;

import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests the class {@link InetAddressFactoryImpl}.
 */
public class InetAddressFactoryImplTest {

    private InetAddressFactoryImpl factory;

    @Before
    public void setUp() {
        factory = new InetAddressFactoryImpl();
    }

    @Test
    public void createByName_withHostname_returnsResolvedDeterminedInetAddress() throws UnknownHostException {
        final InetAddress inetAddress = factory.createByName("localhost");
        assertArrayEquals(new byte[] {127, 0, 0, 1}, inetAddress.getAddress());
    }

    @Test
    public void createByName_withIPAddress_returnsInetAddress() throws UnknownHostException {
        final InetAddress inetAddress = factory.createByName("91.198.174.192");
        assertArrayEquals(new byte[] {91, -58, -82, -64}, inetAddress.getAddress());
    }

    @Test(expected = NullPointerException.class)
    public void createByName_withoutHost_throwsNullPointerException() throws UnknownHostException {
        factory.createByName(null);
    }

}
