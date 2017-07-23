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
        configuration = new HttpHeaderRequestConfigurationImpl(60000, 2048, 100);
    }

    @Test
    public void getReceiveTimeout_returnsConfiguredReceiveTimeout() {
        assertEquals(60000, configuration.getReceiveTimeout());
    }

    @Test
    public void getRequestLineSizeLimit_returnsConfiguredSizeLimit() {
        assertEquals(2048, configuration.getRequestLineSizeLimit());
    }

    @Test
    public void getFieldsLimit_returnsConfiguredFieldsLimit() {
        assertEquals(100, configuration.getFieldsLimit());
    }

}
