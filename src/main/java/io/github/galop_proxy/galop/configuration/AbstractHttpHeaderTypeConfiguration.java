package io.github.galop_proxy.galop.configuration;

abstract class AbstractHttpHeaderTypeConfiguration implements HttpHeaderTypeConfiguration {

    private final long receiveTimeout;
    private final int maxSize;

    AbstractHttpHeaderTypeConfiguration(final long receiveTimeout, final int maxSize) {
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