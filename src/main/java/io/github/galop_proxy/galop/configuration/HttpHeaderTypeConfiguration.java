package io.github.galop_proxy.galop.configuration;

interface HttpHeaderTypeConfiguration {

    long getReceiveTimeout();

    int getFieldsLimit();

    int getFieldSizeLimit();

}
