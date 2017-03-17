package io.github.sebastianschmidt.galop.administration;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class AdministrationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MonitorFactory.class).to(MonitorFactoryImpl.class).in(Singleton.class);
        bind(ShutdownHandlerFactory.class).to(ShutdownHandlerFactoryImpl.class).in(Singleton.class);
    }

}
