package io.github.galop_proxy.galop.configuration;

import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_RESPONSE_MAX_SIZE;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseMaxSize;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseTimeout;
import static java.util.Objects.requireNonNull;

final class HttpHeaderResponseConfigurationFactoryImpl implements HttpHeaderResponseConfigurationFactory {

    @Override
    public HttpHeaderResponseConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final long receiveTimeout = parseResponseReceiveTimeout(properties);
        final int maxSize = parseResponseMaxSize(properties);
        return new HttpHeaderResponseConfigurationImpl(receiveTimeout, maxSize);
    }

    private long parseResponseReceiveTimeout(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseTimeout(properties, HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT,
                ConfigurationDefaults.HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT);
    }

    private int parseResponseMaxSize(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseMaxSize(properties, HTTP_HEADER_RESPONSE_MAX_SIZE,
                ConfigurationDefaults.HTTP_HEADER_RESPONSE_MAX_SIZE);
    }

}
