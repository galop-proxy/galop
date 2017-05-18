package io.github.galop_proxy.galop.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfigurationFileLoader.class).to(ConfigurationFileLoaderImpl.class).in(Singleton.class);
        bind(ProxyConfigurationFactory.class).to(ProxyConfigurationFactoryImpl.class).in(Singleton.class);
        bind(TargetConfigurationFactory.class).to(TargetConfigurationFactoryImpl.class).in(Singleton.class);
        bind(HttpConnectionConfigurationFactory.class).to(HttpConnectionConfigurationFactoryImpl.class).in(Singleton.class);
        bind(HttpHeaderRequestConfigurationFactory.class).to(HttpHeaderRequestConfigurationFactoryImpl.class).in(Singleton.class);
        bind(HttpHeaderResponseConfigurationFactory.class).to(HttpHeaderResponseConfigurationFactoryImpl.class).in(Singleton.class);
    }

}
