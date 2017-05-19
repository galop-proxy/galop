package io.github.galop_proxy.galop.configuration;

public class InvalidConfigurationException extends Exception {

    public static InvalidConfigurationException missingProperty(final String propertyKey) {
        return new InvalidConfigurationException("Property " + propertyKey + " is missing.");
    }

    public InvalidConfigurationException(final String message) {
        super(message);
    }

}
