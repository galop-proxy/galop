package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.PortNumber;

import java.net.InetAddress;

import static java.util.Objects.requireNonNull;

final class ConfigurationImpl implements Configuration {

    private final PortNumber proxyPort;
    private final InetAddress targetAddress;
    private final PortNumber targetPort;
    private long targetConnectionTimeout = ConfigurationDefaults.TARGET_CONNECTION_TIMEOUT;
    private int maxHttpHeaderSize = ConfigurationDefaults.MAX_HTTP_HEADER_SIZE;
    private long connectionHandlersLogInterval = ConfigurationDefaults.CONNECTION_HANDLERS_LOG_INTERVAL;
    private long connectionHandlersTerminationTimeout = ConfigurationDefaults.CONNECTION_HANDLERS_TERMINATION_TIMEOUT;

    ConfigurationImpl(final PortNumber proxyPort, final InetAddress targetAddress, final PortNumber targetPort) {
        this.proxyPort = requireNonNull(proxyPort, "proxyPort must not be null.");
        this.targetAddress = requireNonNull(targetAddress, "targetAddress must not be null.");
        this.targetPort = requireNonNull(targetPort, "targetPort must not be null.");
    }

    @Override
    public PortNumber getProxyPort() {
        return proxyPort;
    }

    @Override
    public InetAddress getTargetAddress() {
        return targetAddress;
    }

    @Override
    public PortNumber getTargetPort() {
        return targetPort;
    }

    @Override
    public long getTargetConnectionTimeout() {
        return targetConnectionTimeout;
    }

    void setTargetConnectionTimeout(final long targetConnectionTimeout) {
        this.targetConnectionTimeout = targetConnectionTimeout;
    }

    @Override
    public int getMaxHttpHeaderSize() {
        return maxHttpHeaderSize;
    }

    void setMaxHttpHeaderSize(final int maxHttpHeaderSize) {
        this.maxHttpHeaderSize = maxHttpHeaderSize;
    }

    @Override
    public long getConnectionHandlersLogInterval() {
        return connectionHandlersLogInterval;
    }

    void setConnectionHandlersLogInterval(final long connectionHandlersLogInterval) {
        this.connectionHandlersLogInterval = connectionHandlersLogInterval;
    }

    @Override
    public long getConnectionHandlersTerminationTimeout() {
        return connectionHandlersTerminationTimeout;
    }

    void setConnectionHandlersTerminationTimeout(final long connectionHandlersTerminationTimeout) {
        this.connectionHandlersTerminationTimeout = connectionHandlersTerminationTimeout;
    }

}
