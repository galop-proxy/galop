package io.github.galop_proxy.galop.configuration;

import javax.inject.Inject;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class HttpHeaderConfigurationFactoryImpl implements HttpHeaderConfigurationFactory {

    private final HttpHeaderRequestConfigurationFactory requestConfigurationFactory;
    private final HttpHeaderResponseConfigurationFactory responseConfigurationFactory;

    @Inject
    HttpHeaderConfigurationFactoryImpl(final HttpHeaderRequestConfigurationFactory requestConfigurationFactory,
                                       final HttpHeaderResponseConfigurationFactory responseConfigurationFactory) {
        this.requestConfigurationFactory = requireNonNull(requestConfigurationFactory,
                "requestConfigurationFactory must not be null.");
        this.responseConfigurationFactory = requireNonNull(responseConfigurationFactory,
                "responseConfigurationFactory must not be null.");
    }

    @Override
    public HttpHeaderConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final HttpHeaderRequestConfiguration request = requestConfigurationFactory.parse(properties);
        final HttpHeaderResponseConfiguration response = responseConfigurationFactory.parse(properties);
        return new HttpHeaderConfigurationImpl(request, response);
    }

}
