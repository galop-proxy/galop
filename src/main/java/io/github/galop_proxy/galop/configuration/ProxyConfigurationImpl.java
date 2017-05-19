package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import static java.util.Objects.requireNonNull;

final class ProxyConfigurationImpl implements ProxyConfiguration {

    private final PortNumber port;

    ProxyConfigurationImpl(final PortNumber port) {
        this.port = requireNonNull(port, "port must not be null.");
    }

    @Override
    public PortNumber getPort() {
        return port;
    }

}
