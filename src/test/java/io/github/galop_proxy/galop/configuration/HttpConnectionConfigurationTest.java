package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link HttpConnectionConfiguration}.
 */
public class HttpConnectionConfigurationTest {

    private HttpConnectionConfiguration configuration;

    @Before
    public void setUp() {
        configuration = new HttpConnectionConfigurationImpl(60000, 30000);
    }

    @Test
    public void getLogInterval_returnsConfiguredLogInterval() {
        assertEquals(60000, configuration.getLogInterval());
    }

    @Test
    public void getTerminationTimeout_returnsConfiguredLogInterval() {
        assertEquals(30000, configuration.getTerminationTimeout());
    }

}
