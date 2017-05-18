package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.net.InetAddress;

public interface Configuration {

    PortNumber getProxyPort();

    InetAddress getTargetAddress();

    PortNumber getTargetPort();

    int getTargetConnectionTimeout();

    long getHttpConnectionLogInterval();

    long getHttpConnectionTerminationTimeout();

    long getHttpHeaderRequestReceiveTimeout();

    long getHttpHeaderResponseReceiveTimeout();

    int getMaxHttpHeaderSize();

}
