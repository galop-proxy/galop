package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.util.Map;

final class FactoryUtils {

    static PortNumber parsePortNumber(final Map<String, String> properties, final String propertyKey)
            throws InvalidConfigurationException {

        final String portNumberAsString = properties.get(propertyKey);

        if (portNumberAsString == null) {
            throw InvalidConfigurationException.missingProperty(propertyKey);
        }

        final PortNumber portNumber = PortNumber.parsePortNumber(portNumberAsString);

        if (portNumber == null) {
            throw new InvalidConfigurationException("Property " + propertyKey + " is not a valid port number: "
                    + portNumberAsString);
        }

        return portNumber;

    }

    static long parseTimeout(final Map<String, String> properties, final String propertyKey, final long defaultValue)
            throws InvalidConfigurationException {


        final String timeoutAsString = properties.getOrDefault(propertyKey, Long.toString(defaultValue));

        final long timeout;

        try {
            timeout = Long.parseLong(timeoutAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + propertyKey + " is not a valid number: "
                    + timeoutAsString);
        }

        if (timeout < 0) {
            throw new InvalidConfigurationException("Property " + propertyKey + " must be at least zero: " + timeout);
        }

        return timeout;

    }

    static int parseMaxSize(final Map<String, String> properties, final String propertyKey, final int defaultValue)
            throws InvalidConfigurationException {

        final String maxSizeAsString = properties.getOrDefault(propertyKey, Integer.toString(defaultValue));

        final int maxSize;

        try {
            maxSize = Integer.parseInt(maxSizeAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + propertyKey + " is not a valid number: "
                    + maxSizeAsString);
        }

        if (maxSize < 255) {
            throw new InvalidConfigurationException("Property " + propertyKey + " must be at least 255: "
                    + maxSizeAsString);
        }

        return maxSize;

    }

    private FactoryUtils() {
        throw new AssertionError("No instances");
    }

}
