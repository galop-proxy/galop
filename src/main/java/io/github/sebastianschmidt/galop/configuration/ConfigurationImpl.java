package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.PortNumber;

import java.net.InetAddress;

final class ConfigurationImpl implements Configuration {

    private PortNumber proxyPort;
    private InetAddress targetAddress;
    private PortNumber targetPort;
    private int maxHttpHeaderSize;
    private long connectionHandlersLogInterval;
    private long connectionHandlersTerminationTimeout;

    @Override
    public PortNumber getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final PortNumber proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public InetAddress getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(final InetAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    @Override
    public PortNumber getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(final PortNumber targetPort) {
        this.targetPort = targetPort;
    }

    @Override
    public int getMaxHttpHeaderSize() {
        return maxHttpHeaderSize;
    }

    public void setMaxHttpHeaderSize(final int maxHttpHeaderSize) {
        this.maxHttpHeaderSize = maxHttpHeaderSize;
    }

    @Override
    public long getConnectionHandlersLogInterval() {
        return connectionHandlersLogInterval;
    }

    public void setConnectionHandlersLogInterval(final long connectionHandlersLogInterval) {
        this.connectionHandlersLogInterval = connectionHandlersLogInterval;
    }

    @Override
    public long getConnectionHandlersTerminationTimeout() {
        return connectionHandlersTerminationTimeout;
    }

    public void setConnectionHandlersTerminationTimeout(final long connectionHandlersTerminationTimeout) {
        this.connectionHandlersTerminationTimeout = connectionHandlersTerminationTimeout;
    }
}
