package io.github.galop_proxy.galop.starter;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.github.galop_proxy.galop.commons.CommonsModule;
import io.github.galop_proxy.galop.configuration.ConfigurationModule;

public final class LoadingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new CommonsModule());
        install(new ConfigurationModule());
        bind(Loader.class).to(LoaderImpl.class).in(Singleton.class);
    }

}
