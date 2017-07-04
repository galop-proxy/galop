package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.api.network.InetAddressFactory;
import io.github.galop_proxy.galop.network.PortNumber;

import javax.inject.Inject;
import java.net.InetAddress;
import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.TARGET_ADDRESS;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.TARGET_CONNECTION_TIMEOUT;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.TARGET_PORT;

final class TargetConfigurationFactoryImpl implements TargetConfigurationFactory {

    private final InetAddressFactory inetAddressFactory;

    @Inject
    TargetConfigurationFactoryImpl(final InetAddressFactory inetAddressFactory) {
        this.inetAddressFactory = checkNotNull(inetAddressFactory, "inetAddressFactory");
    }

    @Override
    public TargetConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        checkNotNull(properties, "properties");
        final InetAddress address = parseAddress(properties);
        final PortNumber port = parsePort(properties);
        final int connectionTimeout = parseConnectionTimeout(properties);
        return new TargetConfigurationImpl(address, port, connectionTimeout);
    }

    private InetAddress parseAddress(final Map<String, String> properties) throws InvalidConfigurationException {

        final String addressAsString = properties.get(TARGET_ADDRESS);

        if (addressAsString == null) {
            throw InvalidConfigurationException.missingProperty(TARGET_ADDRESS);
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

    private PortNumber parsePort(final Map<String, String> properties) throws InvalidConfigurationException {
        return FactoryUtils.parsePortNumber(properties, TARGET_PORT);
    }

    private int parseConnectionTimeout(final Map<String, String> properties) throws InvalidConfigurationException {

        final String connectionTimeoutAsString = properties.getOrDefault(TARGET_CONNECTION_TIMEOUT,
                Integer.toString(ConfigurationDefaults.TARGET_CONNECTION_TIMEOUT));

        final int connectionTimeout;

        try {
            connectionTimeout = Integer.parseInt(connectionTimeoutAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + TARGET_CONNECTION_TIMEOUT
                    + " is not a valid number: " + connectionTimeoutAsString);
        }

        if (connectionTimeout < 0) {
            throw new InvalidConfigurationException("Property " + TARGET_CONNECTION_TIMEOUT
                    + " must be at least zero: " + connectionTimeout);
        }

        return connectionTimeout;

    }

}
