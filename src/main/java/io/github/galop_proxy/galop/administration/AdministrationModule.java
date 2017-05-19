package io.github.galop_proxy.galop.administration;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class AdministrationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Monitor.class).to(MonitorImpl.class).in(Singleton.class);
        bind(ShutdownHandler.class).to(ShutdownHandlerImpl.class).in(Singleton.class);
    }

}
