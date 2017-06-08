package io.github.galop_proxy.galop.configuration;

import com.google.inject.AbstractModule;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;

public final class LoadedConfigurationModule extends AbstractModule {

    private final Configuration configuration;

    public LoadedConfigurationModule(final Configuration configuration) {
        this.configuration = checkNotNull(configuration, "configuration");
    }

    @Override
    protected void configure() {
        bindConfiguration();
        bindProxyConfiguration();
        bindTargetConfiguration();
        bindHttpConfiguration();
    }

    private void bindConfiguration() {
        bind(Configuration.class).toInstance(configuration);
    }

    private void bindProxyConfiguration() {
        bind(ProxyConfiguration.class).toInstance(configuration.getProxy());
    }

    private void bindTargetConfiguration() {
        bind(TargetConfiguration.class).toInstance(configuration.getTarget());
    }

    private void bindHttpConfiguration() {
        bind(HttpConfiguration.class).toInstance(configuration.getHttp());
        bindHttpConnectionConfiguration();
        bindHttpHeaderConfiguration();
    }

    private void bindHttpConnectionConfiguration() {
        bind(HttpConnectionConfiguration.class).toInstance(configuration.getHttp().getConnection());
    }

    private void bindHttpHeaderConfiguration() {
        bind(HttpHeaderConfiguration.class).toInstance(configuration.getHttp().getHeader());
        bind(HttpHeaderRequestConfiguration.class).toInstance(configuration.getHttp().getHeader().getRequest());
        bind(HttpHeaderResponseConfiguration.class).toInstance(configuration.getHttp().getHeader().getResponse());
    }

}
