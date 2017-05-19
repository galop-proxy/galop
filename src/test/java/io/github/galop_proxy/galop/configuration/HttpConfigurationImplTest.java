package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link HttpConfigurationImpl}.
 */
public class HttpConfigurationImplTest {

    private HttpConnectionConfiguration connection;
    private HttpHeaderConfiguration header;
    private HttpConfiguration configuration;

    @Before
    public void setUp() {
        connection = mock(HttpConnectionConfiguration.class);
        header = mock(HttpHeaderConfiguration.class);
        configuration = new HttpConfigurationImpl(connection, header);
    }

    @Test
    public void getConnection_returnsGivenConnectionConfiguration() {
        assertSame(connection, configuration.getConnection());
    }

    @Test
    public void getHeader_returnsGivenHeaderConfiguration() {
        assertSame(header, configuration.getHeader());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpConnectionConfiguration_throwsNullPointerException() {
        new HttpConfigurationImpl(null, header);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderConfiguration_throwsNullPointerException() {
        new HttpConfigurationImpl(connection, null);
    }

}
