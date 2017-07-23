package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link HttpHeaderRequestConfigurationImpl}.
 */
public class HttpHeaderResponseConfigurationImplTest {

    private HttpHeaderResponseConfiguration configuration;

    @Before
    public void setUp() {
        configuration = new HttpHeaderResponseConfigurationImpl(90000, 2048, 100);
    }

    @Test
    public void getReceiveTimeout_returnsConfiguredReceiveTimeout() {
        assertEquals(90000, configuration.getReceiveTimeout());
    }

    @Test
    public void getStatusLineSizeLimit_returnsConfiguredSizeLimit() {
        assertEquals(2048, configuration.getStatusLineSizeLimit());
    }

    @Test
    public void getFieldsLimit_returnsConfiguredFieldsLimit() {
        assertEquals(100, configuration.getFieldsLimit());
    }

}
