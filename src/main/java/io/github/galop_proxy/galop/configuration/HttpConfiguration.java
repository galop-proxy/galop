package io.github.galop_proxy.galop.configuration;

public interface HttpConfiguration {

    HttpConnectionConfiguration getConnection();

    HttpHeaderConfiguration getHeader();

}
