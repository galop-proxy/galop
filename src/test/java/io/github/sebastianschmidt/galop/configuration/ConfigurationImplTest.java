package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link ConfigurationImpl}.
 */
public class ConfigurationImplTest {

    private ConfigurationImpl configuration;

    @Before
    public void setUp() throws UnknownHostException {
        configuration = new ConfigurationImpl();
        configuration.setProxyPort(new PortNumber(8080));
        configuration.setTargetAddress(InetAddress.getLocalHost());
        configuration.setTargetPort(new PortNumber(80));
        configuration.setMaxHttpHeaderSize(1024);
        configuration.setConnectionHandlersLogInterval(10000);
        configuration.setConnectionHandlersTerminationTimeout(60000);
    }

    @Test
    public void getProxyPort_returnsConfiguredProxyPort() {
        assertEquals(8080, configuration.getProxyPort().getValue());
    }

    @Test
    public void getTargetAddress_returnsConfiguredTargetAddress() throws UnknownHostException {
        assertEquals(InetAddress.getLocalHost(), configuration.getTargetAddress());
    }

    @Test
    public void getTargetPort_returnsConfiguredTargetPort() {
        assertEquals(80, configuration.getTargetPort().getValue());
    }

    @Test
    public void getMaxHttpHeaderSize_returnsConfiguredValue() {
        assertEquals(1024, configuration.getMaxHttpHeaderSize());
    }

    @Test
    public void getConnectionHandlersLogInterval_returnsConfiguredValue() {
        assertEquals(10000, configuration.getConnectionHandlersLogInterval());
    }

    @Test
    public void getConnectionHandlersTerminationTimeout_returnsConfiguredValue() {
        assertEquals(60000, configuration.getConnectionHandlersTerminationTimeout());
    }

}
