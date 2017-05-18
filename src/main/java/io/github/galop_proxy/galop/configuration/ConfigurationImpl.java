package io.github.galop_proxy.galop.configuration;

import io.github.galop_proxy.galop.commons.PortNumber;

import java.net.InetAddress;

import static java.util.Objects.requireNonNull;

final class ConfigurationImpl implements Configuration {

    private final PortNumber proxyPort;
    private final InetAddress targetAddress;
    private final PortNumber targetPort;

    private int targetConnectionTimeout = ConfigurationDefaults.TARGET_CONNECTION_TIMEOUT;

    private long connectionHandlersLogInterval = ConfigurationDefaults.HTTP_CONNECTION_LOG_INTERVAL;
    private long connectionHandlersTerminationTimeout = ConfigurationDefaults.HTTP_CONNECTION_TERMINATION_TIMEOUT;

    private long httpRequestHeaderReceiveTimeout = ConfigurationDefaults.HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT;
    private long httpResponseHeaderReceiveTimeout = ConfigurationDefaults.HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT;

    private int maxHttpHeaderSize = ConfigurationDefaults.HTTP_HEADER_MAX_SIZE;

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
    public int getTargetConnectionTimeout() {
        return targetConnectionTimeout;
    }

    void setTargetConnectionTimeout(final int targetConnectionTimeout) {
        this.targetConnectionTimeout = targetConnectionTimeout;
    }

    @Override
    public long getHttpConnectionLogInterval() {
        return connectionHandlersLogInterval;
    }

    void setConnectionHandlersLogInterval(final long connectionHandlersLogInterval) {
        this.connectionHandlersLogInterval = connectionHandlersLogInterval;
    }

    @Override
    public long getHttpConnectionTerminationTimeout() {
        return connectionHandlersTerminationTimeout;
    }

    void setConnectionHandlersTerminationTimeout(final long connectionHandlersTerminationTimeout) {
        this.connectionHandlersTerminationTimeout = connectionHandlersTerminationTimeout;
    }

    @Override
    public long getHttpHeaderRequestReceiveTimeout() {
        return httpRequestHeaderReceiveTimeout;
    }

    void setHttpRequestHeaderReceiveTimeout(final long httpRequestHeaderReceiveTimeout) {
        this.httpRequestHeaderReceiveTimeout = httpRequestHeaderReceiveTimeout;
    }

    @Override
    public long getHttpHeaderResponseReceiveTimeout() {
        return httpResponseHeaderReceiveTimeout;
    }

    void setHttpResponseHeaderReceiveTimeout(final long httpResponseHeaderReceiveTimeout) {
        this.httpResponseHeaderReceiveTimeout = httpResponseHeaderReceiveTimeout;
    }

    @Override
    public int getMaxHttpHeaderSize() {
        return maxHttpHeaderSize;
    }

    void setMaxHttpHeaderSize(final int maxHttpHeaderSize) {
        this.maxHttpHeaderSize = maxHttpHeaderSize;
    }


}
