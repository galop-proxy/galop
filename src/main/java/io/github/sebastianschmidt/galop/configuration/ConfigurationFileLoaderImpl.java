package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.InetAddressFactory;
import io.github.sebastianschmidt.galop.commons.PortNumber;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Properties;

import static io.github.sebastianschmidt.galop.configuration.ConfigurationPropertyKeys.*;
import static java.util.Objects.requireNonNull;

final class ConfigurationFileLoaderImpl implements ConfigurationFileLoader {

    private static class ConfigurationImpl implements Configuration {

        private final PortNumber proxyPort;
        private final InetAddress inetAddress;
        private final PortNumber portNumber;
        private final int maxHttpHeaderSize;
        private final long connectionHandlersLogInterval;
        private final long connectionHandlersTerminationTimeout;

        private ConfigurationImpl(final PortNumber proxyPort, final InetAddress inetAddress,
                                  final PortNumber portNumber, final int maxHttpHeaderSize,
                                  final long connectionHandlersLogInterval,
                                  final long connectionHandlersTerminationTimeout) {
            this.proxyPort = requireNonNull(proxyPort, "proxyPort must not be null.");
            this.inetAddress = requireNonNull(inetAddress, "inetAddress must not be null.");
            this.portNumber = requireNonNull(portNumber, "portNumber must not be null.");
            this.maxHttpHeaderSize = maxHttpHeaderSize;
            this.connectionHandlersLogInterval = connectionHandlersLogInterval;
            this.connectionHandlersTerminationTimeout = connectionHandlersTerminationTimeout;
        }

        @Override
        public PortNumber getProxyPort() {
            return proxyPort;
        }

        @Override
        public InetAddress getTargetAddress() {
            return inetAddress;
        }

        @Override
        public PortNumber getTargetPort() {
            return portNumber;
        }

        @Override
        public int getMaxHttpHeaderSize() {
            return maxHttpHeaderSize;
        }

        @Override
        public long getConnectionHandlersLogInterval() {
            return connectionHandlersLogInterval;
        }

        @Override
        public long getConnectionHandlersTerminationTimeout() {
            return connectionHandlersTerminationTimeout;
        }

    }

    private final InetAddressFactory inetAddressFactory;

    @Inject
    ConfigurationFileLoaderImpl(final InetAddressFactory inetAddressFactory) {
        this.inetAddressFactory = requireNonNull(inetAddressFactory, "inetAddressFactory must not be null.");
    }

    @Override
    public Configuration load(final Path path) throws IOException, InvalidConfigurationException {

        final Properties properties = new Properties();
        properties.load(new FileInputStream(path.toFile()));

        final PortNumber proxyPortNumber = parseProxyPortNumber(properties);
        final InetAddress targetAddress = parseTargetAddress(properties);
        final PortNumber targetPort = parseTargetPortNumber(properties);
        final int maxHttpHeaderSize = parseMaxHttpHeaderSize(properties);
        final long connectionHandlersLogInterval = parseConnectionHandlersLogInterval(properties);
        final long connectionHandlersTerminationTimeout = parseConnectionHandlersTerminationTimeout(properties);

        return new ConfigurationImpl(proxyPortNumber, targetAddress, targetPort, maxHttpHeaderSize,
                connectionHandlersLogInterval, connectionHandlersTerminationTimeout);

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
                    + " is not a valid IP address or hostname: " + addressAsString);
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

    private int parseMaxHttpHeaderSize(final Properties properties) throws InvalidConfigurationException {

        final String maxHttpHeaderSizeAsString = properties.getProperty(MAX_HTTP_HEADER_SIZE,
                Integer.toString(ConfigurationDefaults.MAX_HTTP_HEADER_SIZE));

        final int maxHttpHeaderSize;

        try {
            maxHttpHeaderSize = Integer.parseInt(maxHttpHeaderSizeAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + MAX_HTTP_HEADER_SIZE + " is not a valid number: "
                    + maxHttpHeaderSizeAsString);
        }

        if (maxHttpHeaderSize < 255) {
            throw new InvalidConfigurationException("Property " + MAX_HTTP_HEADER_SIZE + " must be at least 255: "
                    + maxHttpHeaderSize);
        }

        return maxHttpHeaderSize;

    }

    private long parseConnectionHandlersLogInterval(final Properties properties)
            throws InvalidConfigurationException {

        final String intervalAsString = properties.getProperty(CONNECTION_HANDLERS_LOG_INTERVAL,
                Long.toString(ConfigurationDefaults.CONNECTION_HANDLERS_LOG_INTERVAL));

        final long interval;

        try {
            interval = Long.parseLong(intervalAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + CONNECTION_HANDLERS_LOG_INTERVAL
                    + " is not a valid number: " + intervalAsString);
        }

        if (interval < 0) {
            throw new InvalidConfigurationException("Property " + CONNECTION_HANDLERS_LOG_INTERVAL
                    + " must be at least zero: " + interval);
        }

        return interval;

    }


    private long parseConnectionHandlersTerminationTimeout(final Properties properties)
            throws InvalidConfigurationException {

        final String timeoutAsString = properties.getProperty(CONNECTION_HANDLERS_TERMINATION_TIMEOUT,
                Long.toString(ConfigurationDefaults.CONNECTION_HANDLERS_TERMINATION_TIMEOUT));

        final long timeout;

        try {
            timeout = Long.parseLong(timeoutAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + CONNECTION_HANDLERS_TERMINATION_TIMEOUT
                    + " is not a valid number: " + timeoutAsString);
        }

        if (timeout < 0) {
            throw new InvalidConfigurationException("Property " + CONNECTION_HANDLERS_TERMINATION_TIMEOUT
                    + " must be at least zero: " + timeout);
        }

        return timeout;

    }

}
