package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.PortNumber;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link ConfigurationBuilder}.
 */
public class ConfigurationBuilderTest {

    private ConfigurationBuilder builder;

    @Before
    public void setUp() throws UnknownHostException {
        builder = new ConfigurationBuilder();
        builder.setProxyPort(new PortNumber(8080));
        builder.setTargetAddress(InetAddress.getLocalHost());
        builder.setTargetPort(new PortNumber(80));
        builder.setMaxHttpHeaderSize(1024);
        builder.setConnectionHandlersLogInterval(10000);
        builder.setConnectionHandlersTerminationTimeout(60000);
    }

    @Test
    public void build_withCompleteConfigurationProperties_returnsConfiguration() throws UnknownHostException {
        final Configuration configuration = builder.build();
        assertEquals(8080, configuration.getProxyPort().getValue());
        assertEquals(InetAddress.getLocalHost(), configuration.getTargetAddress());
        assertEquals(80, configuration.getTargetPort().getValue());
        assertEquals(1024, configuration.getMaxHttpHeaderSize());
        assertEquals(10000, configuration.getConnectionHandlersLogInterval());
        assertEquals(60000, configuration.getConnectionHandlersTerminationTimeout());
    }

    @Test(expected = NullPointerException.class)
    public void build_withoutProxyPort_throwsNullPointerException() {
        builder.setProxyPort(null);
        builder.build();
    }

    @Test(expected = NullPointerException.class)
    public void build_withoutTargetAddress_throwsNullPointerException() {
        builder.setTargetAddress(null);
        builder.build();
    }

    @Test(expected = NullPointerException.class)
    public void build_withoutTargetPort_throwsNullPointerException() {
        builder.setTargetPort(null);
        builder.build();
    }

}
