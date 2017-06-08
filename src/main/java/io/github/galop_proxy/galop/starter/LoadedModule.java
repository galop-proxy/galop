package io.github.galop_proxy.galop.starter;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.github.galop_proxy.galop.administration.AdministrationModule;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.configuration.LoadedConfigurationModule;
import io.github.galop_proxy.galop.http.HttpModule;
import io.github.galop_proxy.galop.proxy.ProxyModule;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;

final class LoadedModule extends AbstractModule {

    private final Configuration configuration;

    LoadedModule(final Configuration configuration) {
        this.configuration = checkNotNull(configuration, "configuration");
    }

    @Override
    protected void configure() {
        installModules();
        bindStarter();
    }

    private void installModules() {
        install(new LoadedConfigurationModule(configuration));
        install(new HttpModule());
        install(new ProxyModule());
        install(new AdministrationModule());
    }

    private void bindStarter() {
        bind(Starter.class).to(StarterImpl.class).in(Singleton.class);
    }

}
