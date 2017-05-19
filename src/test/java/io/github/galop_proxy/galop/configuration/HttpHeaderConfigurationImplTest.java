package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link HttpHeaderConfigurationImpl}.
 */
public class HttpHeaderConfigurationImplTest {

    private HttpHeaderRequestConfiguration request;
    private HttpHeaderResponseConfiguration response;
    private HttpHeaderConfiguration configuration;

    @Before
    public void setUp() {
        request = mock(HttpHeaderRequestConfiguration.class);
        response = mock(HttpHeaderResponseConfiguration.class);
        configuration = new HttpHeaderConfigurationImpl(request, response);
    }

    @Test
    public void getRequest_returnsGivenRequestConfiguration() {
        assertSame(request, configuration.getRequest());
    }

    @Test
    public void getResponse_returnsGivenResponseConfiguration() {
        assertSame(response, configuration.getResponse());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderRequestConfiguration_throwsNullPointerException() {
        new HttpHeaderConfigurationImpl(null, response);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderResponseConfiguration_throwsNullPointerException() {
        new HttpHeaderConfigurationImpl(request, null);
    }

}
