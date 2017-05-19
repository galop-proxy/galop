package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link HttpHeaderRequestConfigurationImpl}.
 */
public class HttpHeaderRequestConfigurationImplTest {

    private HttpHeaderRequestConfiguration configuration;

    @Before
    public void setUp() {
        configuration = new HttpHeaderRequestConfigurationImpl(60000, 8192);
    }

    @Test
    public void getReceiveTimeout_returnsConfiguredReceiveTimeout() {
        assertEquals(60000, configuration.getReceiveTimeout());
    }

    @Test
    public void getMaxSize_returnsConfiguredMaxSize() {
        assertEquals(8192, configuration.getMaxSize());
    }

}
