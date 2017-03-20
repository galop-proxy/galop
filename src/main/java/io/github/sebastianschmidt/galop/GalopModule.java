package io.github.sebastianschmidt.galop;

import com.google.inject.AbstractModule;
import io.github.sebastianschmidt.galop.administration.AdministrationModule;
import io.github.sebastianschmidt.galop.commons.CommonsModule;
import io.github.sebastianschmidt.galop.configuration.ConfigurationModule;
import io.github.sebastianschmidt.galop.http.HttpModule;
import io.github.sebastianschmidt.galop.proxy.ProxyModule;

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
