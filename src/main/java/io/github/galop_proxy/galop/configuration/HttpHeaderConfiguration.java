package io.github.galop_proxy.galop.configuration;

public interface HttpHeaderConfiguration {

    long getRequestReceiveTimeout();

    long getResponseReceiveTimeout();

    int getRequestMaxSize();

    int getResponseMaxSize();

}
