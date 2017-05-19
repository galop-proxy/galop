package io.github.galop_proxy.galop;

import io.github.galop_proxy.galop.configuration.*;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractConfigurationTest {

    protected Configuration configuration;
    protected ProxyConfiguration proxyConfiguration;
    protected TargetConfiguration targetConfiguration;
    protected HttpConfiguration httpConfiguration;
    protected HttpConnectionConfiguration httpConnectionConfiguration;
    protected HttpHeaderConfiguration httpHeaderConfiguration;
    protected HttpHeaderRequestConfiguration httpHeaderRequestConfiguration;
    protected HttpHeaderResponseConfiguration httpHeaderResponseConfiguration;

    @Before
    public void setUp() {

        configuration = mock(Configuration.class);
        proxyConfiguration = mock(ProxyConfiguration.class);
        targetConfiguration = mock(TargetConfiguration.class);
        httpConfiguration = mock(HttpConfiguration.class);
        httpConnectionConfiguration = mock(HttpConnectionConfiguration.class);
        httpHeaderConfiguration = mock(HttpHeaderConfiguration.class);
        httpHeaderRequestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        httpHeaderResponseConfiguration = mock(HttpHeaderResponseConfiguration.class);

        when(configuration.getProxy()).thenReturn(proxyConfiguration);
        when(configuration.getTarget()).thenReturn(targetConfiguration);
        when(configuration.getHttp()).thenReturn(httpConfiguration);
        when(httpConfiguration.getConnection()).thenReturn(httpConnectionConfiguration);
        when(httpConfiguration.getHeader()).thenReturn(httpHeaderConfiguration);
        when(httpHeaderConfiguration.getRequest()).thenReturn(httpHeaderRequestConfiguration);
        when(httpHeaderConfiguration.getResponse()).thenReturn(httpHeaderResponseConfiguration);

    }

}
