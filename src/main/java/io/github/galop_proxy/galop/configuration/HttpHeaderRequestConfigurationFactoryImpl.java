package io.github.galop_proxy.galop.configuration;

import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_REQUEST_MAX_SIZE;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseTimeout;
import static java.util.Objects.requireNonNull;

final class HttpHeaderRequestConfigurationFactoryImpl implements HttpHeaderRequestConfigurationFactory {

    @Override
    public HttpHeaderRequestConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final long receiveTimeout = parseReceiveTimeout(properties);
        final int maxSize = parseMaxSize(properties);
        return new HttpHeaderRequestConfigurationImpl(receiveTimeout, maxSize);
    }

    private long parseReceiveTimeout(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseTimeout(properties, HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT,
                ConfigurationDefaults.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT);
    }

    private int parseMaxSize(final Map<String, String> properties) throws InvalidConfigurationException {
        return FactoryUtils.parseMaxSize(properties, HTTP_HEADER_REQUEST_MAX_SIZE,
                ConfigurationDefaults.HTTP_HEADER_REQUEST_MAX_SIZE);
    }

}