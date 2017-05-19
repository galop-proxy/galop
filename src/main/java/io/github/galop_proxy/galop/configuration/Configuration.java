package io.github.galop_proxy.galop.configuration;

public interface Configuration {

    ProxyConfiguration getProxy();

    TargetConfiguration getTarget();

    HttpConfiguration getHttp();

}
