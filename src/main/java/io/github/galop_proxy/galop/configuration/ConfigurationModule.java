package io.github.galop_proxy.galop.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfigurationFileLoader.class).to(ConfigurationFileLoaderImpl.class).in(Singleton.class);
    }

}
