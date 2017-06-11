package io.github.galop_proxy.galop.configuration;

import javax.inject.Inject;
import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class HttpConfigurationFactoryImpl implements HttpConfigurationFactory {

    private final HttpConnectionConfigurationFactory httpConnectionConfigurationFactory;
    private final HttpHeaderConfigurationFactory httpHeaderConfigurationFactory;

    @Inject
    HttpConfigurationFactoryImpl(final HttpConnectionConfigurationFactory httpConnectionConfigurationFactory,
                                 final HttpHeaderConfigurationFactory httpHeaderConfigurationFactory) {
        this.httpConnectionConfigurationFactory = checkNotNull(httpConnectionConfigurationFactory,
                "httpConnectionConfigurationFactory");
        this.httpHeaderConfigurationFactory = checkNotNull(httpHeaderConfigurationFactory,
                "httpHeaderConfigurationFactory");
    }

    @Override
    public HttpConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        checkNotNull(properties, "properties");
        final HttpConnectionConfiguration httpConnectionConfiguration = httpConnectionConfigurationFactory.parse(properties);
        final HttpHeaderConfiguration httpHeaderConfiguration = httpHeaderConfigurationFactory.parse(properties);
        return new HttpConfigurationImpl(httpConnectionConfiguration, httpHeaderConfiguration);
    }

}
