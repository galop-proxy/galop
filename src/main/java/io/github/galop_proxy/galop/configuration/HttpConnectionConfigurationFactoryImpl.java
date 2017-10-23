package io.github.galop_proxy.galop.configuration;

import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_CONNECTION_LOG_INTERVAL;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_CONNECTION_TERMINATION_TIMEOUT;
import static io.github.galop_proxy.galop.configuration.FactoryUtils.parseTimeout;

final class HttpConnectionConfigurationFactoryImpl implements HttpConnectionConfigurationFactory {

    @Override
    public HttpConnectionConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        checkNotNull(properties, "properties");
        final long logInterval = parseLogInterval(properties);
        final long terminationTimeout = parseTerminationTimeout(properties);
        return new HttpConnectionConfigurationImpl(logInterval, terminationTimeout);
    }

    private long parseLogInterval(final Map<String, String> properties) throws InvalidConfigurationException {

        final String intervalAsString = properties.getOrDefault(HTTP_CONNECTION_LOG_INTERVAL,
                Long.toString(ConfigurationDefaults.HTTP_CONNECTION_LOG_INTERVAL));

        final long interval;

        try {
            interval = (long) Integer.parseInt(intervalAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + HTTP_CONNECTION_LOG_INTERVAL
                    + " is not a valid number: " + intervalAsString);
        }

        if (interval < 1) {
            throw new InvalidConfigurationException("Property " + HTTP_CONNECTION_LOG_INTERVAL
                    + " must be at least 1: " + interval);
        }

        return interval;

    }

    private long parseTerminationTimeout(final Map<String, String> properties) throws InvalidConfigurationException {
        return parseTimeout(properties, HTTP_CONNECTION_TERMINATION_TIMEOUT,
                ConfigurationDefaults.HTTP_CONNECTION_TERMINATION_TIMEOUT);
    }

}
