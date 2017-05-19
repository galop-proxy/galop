package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link ConfigurationFactoryImpl}.
 */
public class ConfigurationFactoryImplTest {

    private ProxyConfigurationFactory proxyConfigurationFactory;
    private TargetConfigurationFactory targetConfigurationFactory;
    private HttpConfigurationFactory httpConfigurationFactory;
    private ConfigurationFactory factory;

    @Before
    public void setUp() {
        proxyConfigurationFactory = mock(ProxyConfigurationFactory.class);
        targetConfigurationFactory = mock(TargetConfigurationFactory.class);
        httpConfigurationFactory = mock(HttpConfigurationFactory.class);
        factory = new ConfigurationFactoryImpl(proxyConfigurationFactory, targetConfigurationFactory,
                httpConfigurationFactory);
    }

    @Test
    public void parse_withProperties_returnsConfiguration() throws InvalidConfigurationException {

        final Map<String, String> properties = new HashMap<>();

        final ProxyConfiguration proxyConfiguration = mock(ProxyConfiguration.class);
        final TargetConfiguration targetConfiguration = mock(TargetConfiguration.class);
        final HttpConfiguration httpConfiguration = mock(HttpConfiguration.class);

        when(proxyConfigurationFactory.parse(properties)).thenReturn(proxyConfiguration);
        when(targetConfigurationFactory.parse(properties)).thenReturn(targetConfiguration);
        when(httpConfigurationFactory.parse(properties)).thenReturn(httpConfiguration);

        final Configuration configuration = factory.parse(properties);

        assertSame(proxyConfiguration, configuration.getProxy());
        assertSame(targetConfiguration, configuration.getTarget());
        assertSame(httpConfiguration, configuration.getHttp());

    }

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutProxyConfigurationFactory_throwsNullPointerException() {
        new ConfigurationFactoryImpl(null, targetConfigurationFactory, httpConfigurationFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetConfigurationFactory_throwsNullPointerException() {
        new ConfigurationFactoryImpl(proxyConfigurationFactory, null, httpConfigurationFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpConfigurationFactory_throwsNullPointerException() {
        new ConfigurationFactoryImpl(proxyConfigurationFactory, targetConfigurationFactory, null);
    }

}
