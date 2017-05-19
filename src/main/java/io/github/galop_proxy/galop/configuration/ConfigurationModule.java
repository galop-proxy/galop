package io.github.galop_proxy.galop.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfigurationFileLoader.class).to(ConfigurationFileLoaderImpl.class).in(Singleton.class);
        bindConfigurationFactories();
    }

    private void bindConfigurationFactories() {
        bindConfigurationFactory();
        bindProxyConfigurationFactory();
        bindTargetConfigurationFactory();
        bindHttpConfigurationFactories();
    }

    private void bindConfigurationFactory() {
        bind(ConfigurationFactory.class).to(ConfigurationFactoryImpl.class).in(Singleton.class);
    }

    private void bindProxyConfigurationFactory() {
        bind(ProxyConfigurationFactory.class).to(ProxyConfigurationFactoryImpl.class).in(Singleton.class);
    }

    private void bindTargetConfigurationFactory() {
        bind(TargetConfigurationFactory.class).to(TargetConfigurationFactoryImpl.class).in(Singleton.class);
    }

    private void bindHttpConfigurationFactories() {
        bind(HttpConfigurationFactory.class).to(HttpConfigurationFactoryImpl.class).in(Singleton.class);
        bind(HttpConnectionConfigurationFactory.class).to(HttpConnectionConfigurationFactoryImpl.class).in(Singleton.class);
        bindHttpHeaderConfigurationFactories();
    }

    private void bindHttpHeaderConfigurationFactories() {
        bind(HttpHeaderConfigurationFactory.class).to(HttpHeaderConfigurationFactoryImpl.class).in(Singleton.class);
        bind(HttpHeaderRequestConfigurationFactory.class).to(HttpHeaderRequestConfigurationFactoryImpl.class).in(Singleton.class);
        bind(HttpHeaderResponseConfigurationFactory.class).to(HttpHeaderResponseConfigurationFactoryImpl.class).in(Singleton.class);
    }

}
