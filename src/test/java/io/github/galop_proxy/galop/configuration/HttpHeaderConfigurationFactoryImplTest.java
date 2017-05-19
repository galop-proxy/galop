package io.github.galop_proxy.galop.configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link HttpHeaderConfigurationFactoryImpl}.
 */
public class HttpHeaderConfigurationFactoryImplTest {

    private HttpHeaderRequestConfigurationFactory requestConfigurationFactory;
    private HttpHeaderResponseConfigurationFactory responseConfigurationFactory;
    private HttpHeaderConfigurationFactory factory;

    @Before
    public void setUp() {
        requestConfigurationFactory = mock(HttpHeaderRequestConfigurationFactory.class);
        responseConfigurationFactory = mock(HttpHeaderResponseConfigurationFactory.class);
        factory = new HttpHeaderConfigurationFactoryImpl(requestConfigurationFactory, responseConfigurationFactory);
    }

    @Test
    public void parse_withProperties_returnsConfiguration() throws InvalidConfigurationException {

        final Map<String, String> properties = new HashMap<>();

        final HttpHeaderRequestConfiguration requestConfiguration = mock(HttpHeaderRequestConfiguration.class);
        final HttpHeaderResponseConfiguration responseConfiguration = mock(HttpHeaderResponseConfiguration.class);

        when(requestConfigurationFactory.parse(properties)).thenReturn(requestConfiguration);
        when(responseConfigurationFactory.parse(properties)).thenReturn(responseConfiguration);

        final HttpHeaderConfiguration configuration = factory.parse(properties);

        assertSame(requestConfiguration, configuration.getRequest());
        assertSame(responseConfiguration, configuration.getResponse());

    }

    @Test(expected = NullPointerException.class)
    public void parse_withoutProperties_throwsNullPointerException() throws InvalidConfigurationException {
        factory.parse(null);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderRequestConfigurationFactory_throwsNullPointerException() {
        new HttpHeaderConfigurationFactoryImpl(null, responseConfigurationFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutHttpHeaderResponseConfigurationFactory_throwsNullPointerException() {
        new HttpHeaderConfigurationFactoryImpl(requestConfigurationFactory, null);
    }

}
