package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.api.network.PortNumber;

import java.net.InetAddress;

public interface TargetConfiguration {

    InetAddress getAddress();

    PortNumber getPort();

    int getConnectionTimeout();

}
