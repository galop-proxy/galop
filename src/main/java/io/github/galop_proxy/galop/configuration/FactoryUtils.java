package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.network.PortNumber;

import java.util.Map;

final class FactoryUtils {

    private static final int SIZE_LIMIT_MIN = 64;
    private static final int SIZE_LIMIT_MAX = 65536;

    private static final int FIELDS_LIMIT_MIN = 1;
    private static final int FIELDS_LIMIT_MAX = 65536;

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
            throw new InvalidConfigurationException("Property " + propertyKey + " must be at least 255: " + maxSize);
        }

        return maxSize;

    }

    static int parseSizeLimit(final Map<String, String> properties, final String propertyKey, final int defaultValue)
            throws InvalidConfigurationException {
        return parseRange(properties, propertyKey, defaultValue, SIZE_LIMIT_MIN, SIZE_LIMIT_MAX);
    }

    static int parseFieldsLimit(final Map<String, String> properties, final String propertyKey, final int defaultValue)
            throws InvalidConfigurationException {
        return parseRange(properties, propertyKey, defaultValue, FIELDS_LIMIT_MIN, FIELDS_LIMIT_MAX);
    }

    private static int parseRange(final Map<String, String> properties, final String propertyKey, final int defaultValue,
                                  final int minValue, final int maxValue)
            throws InvalidConfigurationException {

        final String valueAsString = properties.getOrDefault(propertyKey, Integer.toString(defaultValue));

        final int value;

        try {
            value = Integer.parseInt(valueAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + propertyKey + " is not a valid number: "
                    + valueAsString);
        }

        if (value < minValue || value > maxValue) {
            throw new InvalidConfigurationException("Property " + propertyKey + " must be in range "
                    + "[" + minValue + ".." + maxValue + "]: " + value);
        }

        return value;

    }

    private FactoryUtils() {
        throw new AssertionError("No instances");
    }

}
