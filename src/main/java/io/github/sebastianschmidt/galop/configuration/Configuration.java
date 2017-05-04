package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.PortNumber;

import java.net.InetAddress;

public interface Configuration {

    PortNumber getProxyPort();

    InetAddress getTargetAddress();

    PortNumber getTargetPort();

    long getTargetConnectionTimeout();

    int getMaxHttpHeaderSize();

    long getConnectionHandlersLogInterval();

    long getConnectionHandlersTerminationTimeout();

}
