package io.github.sebastianschmidt.galop.proxy;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class ProxyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ServerFactory.class).to(ServerFactoryImpl.class).in(Singleton.class);
        bind(ConnectionHandlerFactory.class).to(ConnectionHandlerFactoryImpl.class).in(Singleton.class);
    }

}
