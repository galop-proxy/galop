package io.github.galop_proxy.galop.configuration;

import javax.inject.Inject;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class HttpConfigurationFactoryImpl implements HttpConfigurationFactory {

    private final HttpConnectionConfigurationFactory httpConnectionConfigurationFactory;
    private final HttpHeaderConfigurationFactory httpHeaderConfigurationFactory;

    @Inject
    HttpConfigurationFactoryImpl(final HttpConnectionConfigurationFactory httpConnectionConfigurationFactory,
                                 final HttpHeaderConfigurationFactory httpHeaderConfigurationFactory) {
        this.httpConnectionConfigurationFactory = requireNonNull(httpConnectionConfigurationFactory,
                "httpConnectionConfigurationFactory must not be null.");
        this.httpHeaderConfigurationFactory = requireNonNull(httpHeaderConfigurationFactory,
                "httpHeaderConfigurationFactory must not be null.");
    }

    @Override
    public HttpConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final HttpConnectionConfiguration httpConnectionConfiguration = httpConnectionConfigurationFactory.parse(properties);
        final HttpHeaderConfiguration httpHeaderConfiguration = httpHeaderConfigurationFactory.parse(properties);
        return new HttpConfigurationImpl(httpConnectionConfiguration, httpHeaderConfiguration);
    }

}
