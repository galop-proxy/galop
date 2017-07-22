package io.github.galop_proxy.galop.configuration;

import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.*;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseMaxSize;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseSizeLimit;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseTimeout;

final class HttpHeaderRequestConfigurationFactoryImpl implements HttpHeaderRequestConfigurationFactory {

    @Override
    public HttpHeaderRequestConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {

        checkNotNull(properties, "properties");

        final long receiveTimeout = parseReceiveTimeout(properties);
        final int requestLineSizeLimit = parseRequestLineSizeLimit(properties);
        final int fieldsLimit = parseFieldsLimit(properties);
        final int maxSize = parseReceiveMaxSize(properties);

        return new HttpHeaderRequestConfigurationImpl(receiveTimeout, requestLineSizeLimit, fieldsLimit, maxSize);

    }

    private long parseReceiveTimeout(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseTimeout(properties, HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT,
                ConfigurationDefaults.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT);
    }

    private int parseRequestLineSizeLimit(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseSizeLimit(properties, HTTP_HEADER_REQUEST_REQUEST_LINE_SIZE_LIMIT,
                ConfigurationDefaults.HTTP_HEADER_REQUEST_REQUEST_LINE_SIZE_LIMIT);
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
