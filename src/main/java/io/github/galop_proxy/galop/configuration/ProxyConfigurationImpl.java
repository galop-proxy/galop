package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import static java.util.Objects.requireNonNull;

final class ProxyConfigurationImpl implements ProxyConfiguration {

    private final PortNumber port;
    private final int backlogSize;

    ProxyConfigurationImpl(final PortNumber port, final int backlogSize) {
        this.port = requireNonNull(port, "port must not be null.");
        this.backlogSize = backlogSize;
    }

    @Override
    public PortNumber getPort() {
        return port;
    }

    @Override
    public int getBacklogSize() {
        return backlogSize;
    }

}
