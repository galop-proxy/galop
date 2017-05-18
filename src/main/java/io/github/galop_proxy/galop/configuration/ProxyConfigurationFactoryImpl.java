package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.util.Map;

import static io.github.galop_proxy.galop.configuration.ConfigurationPropertyKeys.PROXY_PORT;
import static java.util.Objects.requireNonNull;

final class ProxyConfigurationFactoryImpl implements ProxyConfigurationFactory {

    @Override
    public ProxyConfiguration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final PortNumber port = parsePort(properties);
        return new ProxyConfigurationImpl(port);
    }

    private PortNumber parsePort(final Map<String, String> properties) throws InvalidConfigurationException {
        return FactoryUtils.parsePortNumber(properties, PROXY_PORT);
    }

}
