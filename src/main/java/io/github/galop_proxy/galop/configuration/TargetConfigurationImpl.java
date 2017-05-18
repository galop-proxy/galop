package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.net.InetAddress;

import static java.util.Objects.requireNonNull;

final class TargetConfigurationImpl implements TargetConfiguration {

    private final InetAddress address;
    private final PortNumber port;
    private final int connectionTimeout;

    TargetConfigurationImpl(final InetAddress address, final PortNumber port, final int connectionTimeout) {
        this.address = requireNonNull(address, "address must not be null.");
        this.port = requireNonNull(port, "port must not be null.");
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public PortNumber getPort() {
        return port;
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

}
