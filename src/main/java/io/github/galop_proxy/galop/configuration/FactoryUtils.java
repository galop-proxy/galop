package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.util.Map;

final class FactoryUtils {

    private FactoryUtils() {
        throw new AssertionError("No instances");
    }

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

}
