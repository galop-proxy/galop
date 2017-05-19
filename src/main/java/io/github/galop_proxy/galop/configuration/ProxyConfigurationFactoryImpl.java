package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.InetAddressFactory;
import io.github.galop_proxy.galop.commons.PortNumber;

import javax.inject.Inject;
import java.net.InetAddress;
import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.*;
import static java.util.Objects.requireNonNull;

final class ProxyConfigurationFactoryImpl implements ProxyConfigurationFactory {

    private final InetAddressFactory inetAddressFactory;

    @Inject
    ProxyConfigurationFactoryImpl(final InetAddressFactory inetAddressFactory) {
        this.inetAddressFactory = requireNonNull(inetAddressFactory, "inetAddressFactory must not be null.");
    }

    @Override
    public ProxyConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final PortNumber port = parsePort(properties);
        final int backlogSize = parseBacklogSize(properties);
        final InetAddress bindAddress = parseBindAddress(properties);
        return new ProxyConfigurationImpl(port, backlogSize, bindAddress);
    }

    private PortNumber parsePort(final Map<String, String> properties) throws InvalidConfigurationException {
        return FactoryUtils.parsePortNumber(properties, PROXY_PORT);
    }

    private int parseBacklogSize(final Map<String, String> properties) throws InvalidConfigurationException {

        final String backlogSizeAsString = properties.getOrDefault(PROXY_BACKLOG_SIZE,
                Integer.toString(ConfigurationDefaults.PROXY_BACKLOG_SIZE));

        final int backlogSize;

        try {
            backlogSize = Integer.parseInt(backlogSizeAsString);
        } catch (final NumberFormatException ex) {
            throw new InvalidConfigurationException("Property " + PROXY_BACKLOG_SIZE + " is not a valid number: "
                    + backlogSizeAsString);
        }

        if (backlogSize < 1) {
            throw new InvalidConfigurationException("Property " + PROXY_BACKLOG_SIZE + " must be at least 1: "
                    + backlogSize);
        }

        return backlogSize;

    }

    private InetAddress parseBindAddress(final Map<String, String> properties) throws InvalidConfigurationException {

        final String addressAsString = properties.get(PROXY_BIND_ADDRESS);

        if (addressAsString == null) {
            return ConfigurationDefaults.PROXY_BIND_ADDRESS;
        }

        final InetAddress address;

        try {
            address = inetAddressFactory.createByName(addressAsString);
        } catch (final Exception ex) {
            throw new InvalidConfigurationException("Property " + PROXY_BIND_ADDRESS
                    + " is not a valid IP address or hostname: " + ex.getMessage());
        }

        return address;

    }

}
