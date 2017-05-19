package io.github.galop_proxy.galop.configuration;

final class HttpHeaderRequestConfigurationImpl implements HttpHeaderRequestConfiguration {

    private final long receiveTimeout;
    private final int maxSize;

    HttpHeaderRequestConfigurationImpl(final long receiveTimeout, final int maxSize) {
        this.receiveTimeout = receiveTimeout;
        this.maxSize = maxSize;
    }

    @Override
    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

}
