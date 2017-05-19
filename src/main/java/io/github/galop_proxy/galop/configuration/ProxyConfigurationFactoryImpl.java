package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.PROXY_BACKLOG_SIZE;
import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.PROXY_PORT;
import static java.util.Objects.requireNonNull;

final class ProxyConfigurationFactoryImpl implements ProxyConfigurationFactory {

    @Override
    public ProxyConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final PortNumber port = parsePort(properties);
        final int backlogSize = parseBacklogSize(properties);
        return new ProxyConfigurationImpl(port, backlogSize);
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

}
