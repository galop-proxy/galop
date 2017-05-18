package io.github.galop_proxy.galop.configuration;

final class HttpConnectionConfigurationImpl implements HttpConnectionConfiguration {

    private final long logInterval;
    private final long terminationTimeout;

    HttpConnectionConfigurationImpl(final long logInterval, final long terminationTimeout) {
        this.logInterval = logInterval;
        this.terminationTimeout = terminationTimeout;
    }

    @Override
    public long getLogInterval() {
        return logInterval;
    }

    @Override
    public long getTerminationTimeout() {
        return terminationTimeout;
    }

}
