package io.github.sebastianschmidt.galop.commons;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class CommonsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SocketFactory.class).to(SocketFactoryImpl.class).in(Singleton.class);
        bind(ServerSocketFactory.class).to(ServerSocketFactoryImpl.class).in(Singleton.class);
    }

}
