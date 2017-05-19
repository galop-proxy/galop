package io.github.galop_proxy.galop.configuration;

import com.google.inject.AbstractModule;

import static java.util.Objects.requireNonNull;

public final class LoadedConfigurationModule extends AbstractModule {

    private final Configuration configuration;

    public LoadedConfigurationModule(final Configuration configuration) {
        this.configuration = requireNonNull(configuration, "configuration must not be null.");
    }

    @Override
    protected void configure() {
        bind(Configuration.class).toInstance(configuration);
        bind(ProxyConfiguration.class).toInstance(configuration.getProxy());
        bind(TargetConfiguration.class).toInstance(configuration.getTarget());
        bind(HttpConfiguration.class).toInstance(configuration.getHttp());
        bind(HttpConnectionConfiguration.class).toInstance(configuration.getHttp().getConnection());
        bind(HttpHeaderConfiguration.class).toInstance(configuration.getHttp().getHeader());
        bind(HttpHeaderRequestConfiguration.class).toInstance(configuration.getHttp().getHeader().getRequest());
        bind(HttpHeaderResponseConfiguration.class).toInstance(configuration.getHttp().getHeader().getResponse());
    }

}
