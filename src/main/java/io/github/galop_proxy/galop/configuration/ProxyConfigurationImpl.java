package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.net.InetAddress;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;

final class ProxyConfigurationImpl implements ProxyConfiguration {

    private final PortNumber port;
    private final int backlogSize;
    private final InetAddress bindAddress;

    ProxyConfigurationImpl(final PortNumber port, final int backlogSize, final InetAddress bindAddress) {
        this.port = checkNotNull(port, "port");
        this.backlogSize = backlogSize;
        this.bindAddress = bindAddress;
    }

    @Override
    public PortNumber getPort() {
        return port;
    }

    @Override
    public int getBacklogSize() {
        return backlogSize;
    }

    @Override
    public InetAddress getBindAddress() {
        return bindAddress;
    }

}
