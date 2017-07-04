package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.network.PortNumber;

import java.net.InetAddress;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class TargetConfigurationImpl implements TargetConfiguration {

    private final InetAddress address;
    private final PortNumber port;
    private final int connectionTimeout;

    TargetConfigurationImpl(final InetAddress address, final PortNumber port, final int connectionTimeout) {
        this.address = checkNotNull(address, "address");
        this.port = checkNotNull(port, "port");
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
