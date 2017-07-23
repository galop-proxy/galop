package io.github.galop_proxy.galop.configuration;

import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.*;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseSizeLimit;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseTimeout;

final class HttpHeaderResponseConfigurationFactoryImpl implements HttpHeaderResponseConfigurationFactory {

    @Override
    public HttpHeaderResponseConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {

        checkNotNull(properties, "properties");

        final long receiveTimeout = parseResponseReceiveTimeout(properties);
        final int statusLineSizeLimit = parseStatusLineSizeLimit(properties);
        final int fieldsLimit = parseFieldsLimit(properties);
        final int fieldSizeLimit = parseFieldSizeLimit(properties);

        return new HttpHeaderResponseConfigurationImpl(receiveTimeout, statusLineSizeLimit, fieldsLimit, fieldSizeLimit);

    }

    private long parseResponseReceiveTimeout(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseTimeout(properties, HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT,
                ConfigurationDefaults.HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT);
    }

    private int parseStatusLineSizeLimit(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseSizeLimit(properties, HTTP_HEADER_RESPONSE_STATUS_LINE_SIZE_LIMIT,
                ConfigurationDefaults.HTTP_HEADER_RESPONSE_STATUS_LINE_SIZE_LIMIT);
    }

    private int parseFieldsLimit(final Map<String, String> properties) throws InvalidConfigurationException {
        return FactoryUtils.parseFieldsLimit(properties, HTTP_HEADER_RESPONSE_FIELDS_LIMIT,
                ConfigurationDefaults.HTTP_HEADER_RESPONSE_FIELDS_LIMIT);
    }

    private int parseFieldSizeLimit(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseSizeLimit(properties, HTTP_HEADER_RESPONSE_FIELD_SIZE_LIMIT,
                ConfigurationDefaults.HTTP_HEADER_RESPONSE_FIELD_SIZE_LIMIT);
    }

}
