package io.github.galop_proxy.galop.configuration;

import javax.inject.Inject;
import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class HttpHeaderConfigurationFactoryImpl implements HttpHeaderConfigurationFactory {

    private final HttpHeaderRequestConfigurationFactory requestConfigurationFactory;
    private final HttpHeaderResponseConfigurationFactory responseConfigurationFactory;

    @Inject
    HttpHeaderConfigurationFactoryImpl(final HttpHeaderRequestConfigurationFactory requestConfigurationFactory,
                                       final HttpHeaderResponseConfigurationFactory responseConfigurationFactory) {
        this.requestConfigurationFactory = checkNotNull(requestConfigurationFactory, "requestConfigurationFactory");
        this.responseConfigurationFactory = checkNotNull(responseConfigurationFactory, "responseConfigurationFactory");
    }

    @Override
    public HttpHeaderConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        checkNotNull(properties, "properties");
        final HttpHeaderRequestConfiguration request = requestConfigurationFactory.parse(properties);
        final HttpHeaderResponseConfiguration response = responseConfigurationFactory.parse(properties);
        return new HttpHeaderConfigurationImpl(request, response);
    }

}
