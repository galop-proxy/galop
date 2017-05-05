package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.InetAddressFactory;
import io.github.galop_proxy.galop.commons.PortNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

final class ConfigurationFileLoaderImpl implements ConfigurationFileLoader {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationFileLoader.class);

    private final InetAddressFactory inetAddressFactory;

    @Inject
    ConfigurationFileLoaderImpl(final InetAddressFactory inetAddressFactory) {
        this.inetAddressFactory = requireNonNull(inetAddressFactory, "inetAddressFactory must not be null.");
    }

    @Override
    public Configuration load(final Path path) throws IOException, InvalidConfigurationException {

        LOGGER.info("Loading configuration file: " + path.toAbsolutePath());

        try {

            final Properties properties = new Properties();
            properties.load(new FileInputStream(path.toFile()));

            final ConfigurationImpl configuration = parseRequiredProperties(properties);
            parseOptionalProperties(properties, configuration);
            logResult(configuration);
            return configuration;

        } catch (final Exception ex) {
            LOGGER.error("Could not parse configuration file: " + ex.getMessage());
            throw ex;
        }

    }

    private ConfigurationImpl parseRequiredProperties(final Properties properties) throws InvalidConfigurationException {

        final PortNumber proxyPort = parseProxyPortNumber(properties);
        final InetAddress targetAddress = parseTargetAddress(properties);
        final PortNumber targetPort = parseTargetPortNumber(properties);

        return new ConfigurationImpl(proxyPort, targetAddress, targetPort);

    }

    private InetAddress parseTargetAddress(final Properties properties) throws InvalidConfigurationException {

        final String addressAsString = properties.getProperty(ConfigurationPropertyKeys.TARGET_ADDRESS);

        if (addressAsString == null) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.TARGET_ADDRESS + " is missing.");
        }

        final InetAddress address;

        try {
            address = inetAddressFactory.createByName(addressAsString);
        } catch (final Exception ex) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.TARGET_ADDRESS
                    + " is not a valid IP address or hostname: " + ex.getMessage());
        }

        return address;

    }

    private PortNumber parseProxyPortNumber(final Properties properties) throws InvalidConfigurationException {
        return parsePortNumber(properties, ConfigurationPropertyKeys.PROXY_PORT);
    }

    private PortNumber parseTargetPortNumber(final Properties properties) throws InvalidConfigurationException {
        return parsePortNumber(properties, ConfigurationPropertyKeys.TARGET_PORT);
    }

    private PortNumber parsePortNumber(final Properties properties, final String propertyKey)
            throws InvalidConfigurationException {

        final String portNumberAsString = properties.getProperty(propertyKey);

        if (portNumberAsString == null) {
            throw new InvalidConfigurationException("Property " + propertyKey + " is missing.");
        }

        final PortNumber portNumber = PortNumber.parsePortNumber(portNumberAsString);

        if (portNumber == null) {
            throw new InvalidConfigurationException("Property " + propertyKey + " is not a valid port number: "
                    + portNumberAsString);
        }

        return portNumber;

    }

    private void parseOptionalProperties(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException {
        parseMaxHttpHeaderSize(properties, configuration);
        parseConnectionHandlersLogInterval(properties, configuration);
        parseConnectionHandlersTerminationTimeout(properties, configuration);
    }

    private void parseMaxHttpHeaderSize(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException {

        final String maxHttpHeaderSizeAsString = properties.getProperty(ConfigurationPropertyKeys.MAX_HTTP_HEADER_SIZE);

        if (maxHttpHeaderSizeAsString == null) {
            return;
        }

        final int maxHttpHeaderSize;

        try {
            maxHttpHeaderSize = Integer.parseInt(maxHttpHeaderSizeAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.MAX_HTTP_HEADER_SIZE + " is not a valid number: "
                    + maxHttpHeaderSizeAsString);
        }

        if (maxHttpHeaderSize < 255) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.MAX_HTTP_HEADER_SIZE + " must be at least 255: "
                    + maxHttpHeaderSize);
        }

        configuration.setMaxHttpHeaderSize(maxHttpHeaderSize);

    }

    private void parseConnectionHandlersLogInterval(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException {

        final String intervalAsString = properties.getProperty(ConfigurationPropertyKeys.CONNECTION_HANDLERS_LOG_INTERVAL);

        if (intervalAsString == null) {
            return;
        }

        final long interval;

        try {
            interval = Long.parseLong(intervalAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.CONNECTION_HANDLERS_LOG_INTERVAL
                    + " is not a valid number: " + intervalAsString);
        }

        if (interval < 0) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.CONNECTION_HANDLERS_LOG_INTERVAL
                    + " must be at least zero: " + interval);
        }

        configuration.setConnectionHandlersLogInterval(interval);

    }


    private void parseConnectionHandlersTerminationTimeout(final Properties properties,
                                                           final ConfigurationImpl configuration)
            throws InvalidConfigurationException {

        final String timeoutAsString = properties.getProperty(ConfigurationPropertyKeys.CONNECTION_HANDLERS_TERMINATION_TIMEOUT);

        if (timeoutAsString == null) {
            return;
        }

        final long timeout;

        try {
            timeout = Long.parseLong(timeoutAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.CONNECTION_HANDLERS_TERMINATION_TIMEOUT
                    + " is not a valid number: " + timeoutAsString);
        }

        if (timeout < 0) {
            throw new InvalidConfigurationException("Property " + ConfigurationPropertyKeys.CONNECTION_HANDLERS_TERMINATION_TIMEOUT
                    + " must be at least zero: " + timeout);
        }

        configuration.setConnectionHandlersTerminationTimeout(timeout);

    }

    private void logResult(final Configuration configuration) {
        LOGGER.info("Loaded configuration:");
        log(ConfigurationPropertyKeys.PROXY_PORT, configuration.getProxyPort());
        log(ConfigurationPropertyKeys.TARGET_ADDRESS, configuration.getTargetAddress());
        log(ConfigurationPropertyKeys.TARGET_PORT, configuration.getTargetPort());
        log(ConfigurationPropertyKeys.CONNECTION_HANDLERS_LOG_INTERVAL, configuration.getConnectionHandlersLogInterval());
        log(ConfigurationPropertyKeys.CONNECTION_HANDLERS_TERMINATION_TIMEOUT, configuration.getConnectionHandlersTerminationTimeout());
        log(ConfigurationPropertyKeys.MAX_HTTP_HEADER_SIZE, configuration.getMaxHttpHeaderSize());
    }

    private void log(final String key, final Object property) {
        LOGGER.info(key + " = " + property);
    }

}
