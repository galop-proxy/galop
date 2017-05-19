package io.github.galop_proxy.galop;

import io.github.galop_proxy.galop.configuration.*;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractConfigurationTest {

    protected Configuration configuration;

    @Before
    public void setUp() {

        configuration = mock(Configuration.class);

        final ProxyConfiguration proxyConfiguration = mock(ProxyConfiguration.class);
        final TargetConfiguration targetConfiguration = mock(TargetConfiguration.class);
        final HttpConfiguration httpConfiguration = mock(HttpConfiguration.class);
        final HttpConnectionConfiguration httpConnectionConfiguration = mock(HttpConnectionConfiguration.class);
        final HttpHeaderConfiguration httpHeaderConfiguration = mock(HttpHeaderConfiguration.class);
        final HttpHeaderRequestConfiguration httpHeaderRequestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        final HttpHeaderResponseConfiguration httpHeaderResponseConfiguration = mock(HttpHeaderResponseConfiguration.class);

        when(configuration.getProxy()).thenReturn(proxyConfiguration);
        when(configuration.getTarget()).thenReturn(targetConfiguration);
        when(configuration.getHttp()).thenReturn(httpConfiguration);
        when(httpConfiguration.getConnection()).thenReturn(httpConnectionConfiguration);
        when(httpConfiguration.getHeader()).thenReturn(httpHeaderConfiguration);
        when(httpHeaderConfiguration.getRequest()).thenReturn(httpHeaderRequestConfiguration);
        when(httpHeaderConfiguration.getResponse()).thenReturn(httpHeaderResponseConfiguration);

    }

}
