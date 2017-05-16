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

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.*;
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

        final String addressAsString = properties.getProperty(TARGET_ADDRESS);

        if (addressAsString == null) {
            throw new InvalidConfigurationException("Property " + TARGET_ADDRESS + " is missing.");
        }

        final InetAddress address;

        try {
            address = inetAddressFactory.createByName(addressAsString);
        } catch (final Exception ex) {
            throw new InvalidConfigurationException("Property " + TARGET_ADDRESS
                    + " is not a valid IP address or hostname: " + ex.getMessage());
        }

        return address;

    }

    private PortNumber parseProxyPortNumber(final Properties properties) throws InvalidConfigurationException {
        return parsePortNumber(properties, PROXY_PORT);
    }

    private PortNumber parseTargetPortNumber(final Properties properties) throws InvalidConfigurationException {
        return parsePortNumber(properties, TARGET_PORT);
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
        parseTargetConnectionTimeout(properties, configuration);
        parseConnectionHandlersLogInterval(properties, configuration);
        parseConnectionHandlersTerminationTimeout(properties, configuration);
        parseHttpRequestHeaderReceiveTimeout(properties, configuration);
        parseHttpResponseHeaderReceiveTimeout(properties, configuration);
        parseMaxHttpHeaderSize(properties, configuration);
    }

    private void parseTargetConnectionTimeout(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException {

        final String targetConnectionTimeoutAsString = properties.getProperty(TARGET_CONNECTION_TIMEOUT);

        if (targetConnectionTimeoutAsString == null) {
            return;
        }

        final int targetConnectionTimeout;

        try {
            targetConnectionTimeout = Integer.parseInt(targetConnectionTimeoutAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + TARGET_CONNECTION_TIMEOUT
                    + " is not a valid number: " + targetConnectionTimeoutAsString);
        }

        if (targetConnectionTimeout < 0) {
            throw new InvalidConfigurationException("Property " + TARGET_CONNECTION_TIMEOUT
                    + " must be at least zero: " + targetConnectionTimeout);
        }

        configuration.setTargetConnectionTimeout(targetConnectionTimeout);

    }

    private void parseConnectionHandlersLogInterval(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException {

        final String intervalAsString = properties.getProperty(HTTP_CONNECTION_LOG_INTERVAL);

        if (intervalAsString == null) {
            return;
        }

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

        configuration.setConnectionHandlersLogInterval(interval);

    }


    private void parseConnectionHandlersTerminationTimeout(final Properties properties,
                                                           final ConfigurationImpl configuration)
            throws InvalidConfigurationException {

        final String timeoutAsString = properties.getProperty(HTTP_CONNECTION_TERMINATION_TIMEOUT);

        if (timeoutAsString == null) {
            return;
        }

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

        configuration.setConnectionHandlersTerminationTimeout(timeout);

    }

    private void parseHttpRequestHeaderReceiveTimeout(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException{

        final Long timeout = parseHttpHeaderReceiveTimeout(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, properties);

        if (timeout != null) {
            configuration.setHttpRequestHeaderReceiveTimeout(timeout);
        }

    }

    private void parseHttpResponseHeaderReceiveTimeout(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException{

        final Long timeout = parseHttpHeaderReceiveTimeout(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, properties);

        if (timeout != null) {
            configuration.setHttpResponseHeaderReceiveTimeout(timeout);
        }

    }

    private Long parseHttpHeaderReceiveTimeout(final String propertyKey, final Properties properties)
            throws InvalidConfigurationException {

        final String timeoutAsString = properties.getProperty(propertyKey);

        if (timeoutAsString == null) {
            return null;
        }

        final long timeout;

        try {
            timeout = Long.parseLong(timeoutAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + propertyKey + " is not a valid number: "
                    + timeoutAsString);
        }

        if (timeout < 1) {
            throw new InvalidConfigurationException("Property " + propertyKey + " must be at least one: " + timeout);
        }

        return timeout;

    }

    private void parseMaxHttpHeaderSize(final Properties properties, final ConfigurationImpl configuration)
            throws InvalidConfigurationException {

        final String maxHttpHeaderSizeAsString = properties.getProperty(HTTP_HEADER_MAX_SIZE);

        if (maxHttpHeaderSizeAsString == null) {
            return;
        }

        final int maxHttpHeaderSize;

        try {
            maxHttpHeaderSize = Integer.parseInt(maxHttpHeaderSizeAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + HTTP_HEADER_MAX_SIZE + " is not a valid number: "
                    + maxHttpHeaderSizeAsString);
        }

        if (maxHttpHeaderSize < 255) {
            throw new InvalidConfigurationException("Property " + HTTP_HEADER_MAX_SIZE + " must be at least 255: "
                    + maxHttpHeaderSize);
        }

        configuration.setMaxHttpHeaderSize(maxHttpHeaderSize);

    }

    private void logResult(final Configuration configuration) {
        LOGGER.info("Loaded configuration:");
        logProxyResult(configuration);
        logTargetResult(configuration);
        logHttpConnectionResult(configuration);
        logHttpHeaderResult(configuration);
    }

    private void logProxyResult(final Configuration configuration) {
        log(PROXY_PORT, configuration.getProxyPort());
    }

    private void logTargetResult(final Configuration configuration) {
        log(TARGET_ADDRESS, configuration.getTargetAddress());
        log(TARGET_PORT, configuration.getTargetPort());
        log(TARGET_CONNECTION_TIMEOUT, configuration.getTargetConnectionTimeout());
    }

    private void logHttpConnectionResult(final Configuration configuration) {
        log(HTTP_CONNECTION_LOG_INTERVAL, configuration.getConnectionHandlersLogInterval());
        log(HTTP_CONNECTION_TERMINATION_TIMEOUT, configuration.getConnectionHandlersTerminationTimeout());
    }

    private void logHttpHeaderResult(final Configuration configuration) {
        log(HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT, configuration.getHttpRequestHeaderReceiveTimeout());
        log(HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT, configuration.getHttpResponseHeaderReceiveTimeout());
        log(HTTP_HEADER_MAX_SIZE, configuration.getMaxHttpHeaderSize());
    }

    private void log(final String key, final Object value) {
        LOGGER.info(key + " = " + value);
    }

}
