package io.github.galop_proxy.galop;

import com.google.inject.AbstractModule;
import io.github.galop_proxy.galop.administration.AdministrationModule;
import io.github.galop_proxy.galop.commons.CommonsModule;
import io.github.galop_proxy.galop.configuration.ConfigurationModule;
import io.github.galop_proxy.galop.http.HttpModule;
import io.github.galop_proxy.galop.proxy.ProxyModule;

final class GalopModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new CommonsModule());
        install(new ConfigurationModule());
        install(new HttpModule());
        install(new ProxyModule());
        install(new AdministrationModule());
        bind(Starter.class);
    }

}
