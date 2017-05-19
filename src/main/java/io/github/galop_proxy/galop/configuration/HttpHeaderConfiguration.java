package io.github.galop_proxy.galop.configuration;

public interface HttpHeaderConfiguration {

    HttpHeaderRequestConfiguration getRequest();

    HttpHeaderResponseConfiguration getResponse();

}
