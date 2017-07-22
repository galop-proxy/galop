package io.github.galop_proxy.galop.configuration;

import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_REQUEST_FIELDS_LIMIT;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_REQUEST_MAX_SIZE;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseMaxSize;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseTimeout;

final class HttpHeaderRequestConfigurationFactoryImpl implements HttpHeaderRequestConfigurationFactory {

    @Override
    public HttpHeaderRequestConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {

        checkNotNull(properties, "properties");

        final long receiveTimeout = parseReceiveTimeout(properties);
        final int fieldsLimit = parseFieldsLimit(properties);
        final int maxSize = parseReceiveMaxSize(properties);

        return new HttpHeaderRequestConfigurationImpl(receiveTimeout, fieldsLimit, maxSize);

    }

    private long parseReceiveTimeout(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseTimeout(properties, HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT,
                ConfigurationDefaults.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT);
    }

    private int parseFieldsLimit(final Map<String, String> properties) throws InvalidConfigurationException {
        return FactoryUtils.parseFieldsLimit(properties, HTTP_HEADER_REQUEST_FIELDS_LIMIT,
                ConfigurationDefaults.HTTP_HEADER_REQUEST_FIELDS_LIMIT);
    }

    private int parseReceiveMaxSize(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseMaxSize(properties, HTTP_HEADER_REQUEST_MAX_SIZE,
                ConfigurationDefaults.HTTP_HEADER_REQUEST_MAX_SIZE);
    }

}
