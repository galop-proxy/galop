package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link HttpConfigurationFactoryImpl}.
 */
public class HttpConfigurationFactoryImplTest {

    private HttpConnectionConfigurationFactory httpConnectionConfigurationFactory;
    private HttpHeaderConfigurationFactory httpHeaderConfigurationFactory;
    private HttpConfigurationFactory factory;

    @Before
    public void setUp() {
        httpConnectionConfigurationFactory = mock(HttpConnectionConfigurationFactory.class);
        httpHeaderConfigurationFactory = mock(HttpHeaderConfigurationFactory.class);
        factory = new HttpConfigurationFactoryImpl(httpConnectionConfigurationFactory, httpHeaderConfigurationFactory);
    }

    @Test
    public void parse_withProperties_returnsConfiguration() throws InvalidConfigurationException {

        final Map<String, String> properties = new HashMap<>();

        final HttpConnectionConfiguration httpConnectionConfiguration = mock(HttpConnectionConfiguration.class);
        final HttpHeaderConfiguration httpHeaderConfiguration = mock(HttpHeaderConfiguration.class);

        when(httpConnectionConfigurationFactory.parse(properties)).thenReturn(httpConnectionConfiguration);
        when(httpHeaderConfigurationFactory.parse(properties)).thenReturn(httpHeaderConfiguration);

        final HttpConfiguration configuration = factory.parse(properties);

        assertSame(httpConnectionConfiguration, configuration.getConnection());
        assertSame(httpHeaderConfiguration, configuration.getHeader());

    }

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpConnectionConfigurationFactory_throwsNullPointerException() {
        new HttpConfigurationFactoryImpl(null, httpHeaderConfigurationFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderConfigurationFactory_throwsNullPointerException() {
        new HttpConfigurationFactoryImpl(httpConnectionConfigurationFactory, null);
    }

}
