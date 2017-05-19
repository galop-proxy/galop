package io.github.galop_proxy.galop.configuration;

import static java.util.Objects.requireNonNull;

final class ConfigurationImpl implements Configuration {

    private final ProxyConfiguration proxy;
    private final TargetConfiguration target;
    private final HttpConfiguration http;

    ConfigurationImpl(final ProxyConfiguration proxy, final TargetConfiguration target, final HttpConfiguration http) {
        this.proxy = requireNonNull(proxy, "proxy must not be null.");
        this.target = requireNonNull(target, "target must not be null.");
        this.http = requireNonNull(http, "http must not be null.");
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
