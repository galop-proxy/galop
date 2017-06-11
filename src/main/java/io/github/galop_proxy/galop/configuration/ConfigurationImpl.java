package io.github.galop_proxy.galop.configuration;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class ConfigurationImpl implements Configuration {

    private final ProxyConfiguration proxy;
    private final TargetConfiguration target;
    private final HttpConfiguration http;

    ConfigurationImpl(final ProxyConfiguration proxy, final TargetConfiguration target, final HttpConfiguration http) {
        this.proxy = checkNotNull(proxy, "proxy");
        this.target = checkNotNull(target, "target");
        this.http = checkNotNull(http, "http");
    }

    @Override
    public ProxyConfiguration getProxy() {
        return proxy;
    }

    @Override
    public TargetConfiguration getTarget() {
        return target;
    }

    @Override
    public HttpConfiguration getHttp() {
        return http;
    }

}
