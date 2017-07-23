package io.github.galop_proxy.galop.configuration;

abstract class AbstractHttpHeaderTypeConfiguration implements HttpHeaderTypeConfiguration {

    private final long receiveTimeout;
    private final int fieldsLimit;
    private final int fieldSizeLimit;

    AbstractHttpHeaderTypeConfiguration(final long receiveTimeout, final int fieldsLimit, final int fieldSizeLimit) {
        this.receiveTimeout = receiveTimeout;
        this.fieldsLimit = fieldsLimit;
        this.fieldSizeLimit = fieldSizeLimit;
    }

    @Override
    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    @Override
    public int getFieldsLimit() {
        return fieldsLimit;
    }

    @Override
    public int getFieldSizeLimit() {
        return fieldSizeLimit;
    }

}
