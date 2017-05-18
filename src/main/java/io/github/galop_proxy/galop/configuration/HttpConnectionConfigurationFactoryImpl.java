package io.github.galop_proxy.galop.configuration;

import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_CONNECTION_LOG_INTERVAL;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.HTTP_CONNECTION_TERMINATION_TIMEOUT;
import static java.util.Objects.requireNonNull;

final class HttpConnectionConfigurationFactoryImpl implements HttpConnectionConfigurationFactory {

    @Override
    public HttpConnectionConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final long logInterval = parseLogInterval(properties);
        final long terminationTimeout = parseTerminationTimeout(properties);
        return new HttpConnectionConfigurationImpl(logInterval, terminationTimeout);
    }

    private long parseLogInterval(final Map<String, String> properties) throws InvalidConfigurationException {

        final String intervalAsString = properties.getOrDefault(HTTP_CONNECTION_LOG_INTERVAL,
                Long.toString(ConfigurationDefaults.HTTP_CONNECTION_LOG_INTERVAL));

        final long interval;

        try {
            interval = Long.parseLong(intervalAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + HTTP_CONNECTION_LOG_INTERVAL
                    + " is not a valid number: " + intervalAsString);
        }

        if (interval < 0) {
            throw new InvalidConfigurationException("Property " + HTTP_CONNECTION_LOG_INTERVAL
                    + " must be at least zero: " + interval);
        }

        return interval;

    }

    private long parseTerminationTimeout(final Map<String, String> properties) throws InvalidConfigurationException {

        final String timeoutAsString = properties.getOrDefault(HTTP_CONNECTION_TERMINATION_TIMEOUT,
                Long.toString(ConfigurationDefaults.HTTP_CONNECTION_TERMINATION_TIMEOUT));

        final long timeout;

        try {
            timeout = Long.parseLong(timeoutAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + HTTP_CONNECTION_TERMINATION_TIMEOUT
                    + " is not a valid number: " + timeoutAsString);
        }

        if (timeout < 0) {
            throw new InvalidConfigurationException("Property " + HTTP_CONNECTION_TERMINATION_TIMEOUT
                    + " must be at least zero: " + timeout);
        }

        return timeout;

    }

}
