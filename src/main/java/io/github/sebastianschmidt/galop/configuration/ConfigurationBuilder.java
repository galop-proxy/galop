package io.github.sebastianschmidt.galop.configuration;

import io.github.sebastianschmidt.galop.commons.PortNumber;

import java.net.InetAddress;

import static java.util.Objects.requireNonNull;

final class ConfigurationBuilder {

    private PortNumber proxyPort;
    private InetAddress targetAddress;
    private PortNumber targetPort;
    private int maxHttpHeaderSize;
    private long connectionHandlersLogInterval;
    private long connectionHandlersTerminationTimeout;

    public void setProxyPort(final PortNumber proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setTargetAddress(final InetAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    public void setTargetPort(final PortNumber targetPort) {
        this.targetPort = targetPort;
    }

    public void setMaxHttpHeaderSize(final int maxHttpHeaderSize) {
        this.maxHttpHeaderSize = maxHttpHeaderSize;
    }

    public void setConnectionHandlersLogInterval(final long connectionHandlersLogInterval) {
        this.connectionHandlersLogInterval = connectionHandlersLogInterval;
    }

    public void setConnectionHandlersTerminationTimeout(final long connectionHandlersTerminationTimeout) {
        this.connectionHandlersTerminationTimeout = connectionHandlersTerminationTimeout;
    }

    public Configuration build() {
        return new ConfigurationImpl(this);
    }

    private static class ConfigurationImpl implements Configuration {

        private final PortNumber proxyPort;
        private final InetAddress targetAddress;
        private final PortNumber targetPort;
        private final int maxHttpHeaderSize;
        private final long connectionHandlersLogInterval;
        private final long connectionHandlersTerminationTimeout;

        private ConfigurationImpl(final ConfigurationBuilder builder) {
            this.proxyPort = requireNonNull(builder.proxyPort, "proxyPort must not be null.");
            this.targetAddress = requireNonNull(builder.targetAddress, "targetAddress must not be null.");
            this.targetPort = requireNonNull(builder.targetPort, "targetPort must not be null.");
            this.maxHttpHeaderSize = builder.maxHttpHeaderSize;
            this.connectionHandlersLogInterval = builder.connectionHandlersLogInterval;
            this.connectionHandlersTerminationTimeout = builder.connectionHandlersTerminationTimeout;
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
        public int getMaxHttpHeaderSize() {
            return maxHttpHeaderSize;
        }

        @Override
        public long getConnectionHandlersLogInterval() {
            return connectionHandlersLogInterval;
        }

        @Override
        public long getConnectionHandlersTerminationTimeout() {
            return connectionHandlersTerminationTimeout;
        }

    }

}
