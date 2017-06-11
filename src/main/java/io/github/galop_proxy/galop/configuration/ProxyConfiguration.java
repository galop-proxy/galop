package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.api.network.PortNumber;

import java.net.InetAddress;

public interface ProxyConfiguration {

    PortNumber getPort();

    int getBacklogSize();

    InetAddress getBindAddress();

}
