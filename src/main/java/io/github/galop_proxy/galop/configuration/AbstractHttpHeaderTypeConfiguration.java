package io.github.galop_proxy.galop.configuration;

abstract class AbstractHttpHeaderTypeConfiguration implements HttpHeaderTypeConfiguration {

    private final long receiveTimeout;
    private final int fieldsLimit;

    AbstractHttpHeaderTypeConfiguration(final long receiveTimeout, final int fieldsLimit) {
        this.receiveTimeout = receiveTimeout;
        this.fieldsLimit = fieldsLimit;
    }

    @Override
    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    @Override
    public int getFieldsLimit() {
        return fieldsLimit;
    }

}
