package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Tests the class {@link ConfigurationImpl}.
 */
public class ConfigurationImplTest {

    private ProxyConfiguration proxy;
    private TargetConfiguration target;
    private HttpConfiguration http;
    private Configuration configuration;

    @Before
    public void setUp() {
        proxy = mock(ProxyConfiguration.class);
        target = mock(TargetConfiguration.class);
        http = mock(HttpConfiguration.class);
        configuration = new ConfigurationImpl(proxy, target, http);
    }

    @Test
    public void getProxy_returnsGivenProxyConfiguration() {
        assertSame(proxy, configuration.getProxy());
    }

    @Test
    public void getTarget_returnsGivenTargetConfiguration() {
        assertSame(target, configuration.getTarget());
    }

    @Test
    public void getHttp_returnsGivenHttpConfiguration() {
        assertSame(http, configuration.getHttp());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutProxyConfiguration_throwsNullPointerException() {
        new ConfigurationImpl(null, target, http);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetConfiguration_throwsNullPointerException() {
        new ConfigurationImpl(proxy, null, http);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpConfiguration_throwsNullPointerException() {
        new ConfigurationImpl(proxy, target, null);
    }

}
