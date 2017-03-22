package io.github.sebastianschmidt.galop.http;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class HttpModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HttpHeaderParser.class).to(HttpHeaderParserImpl.class).in(Singleton.class);
        bind(HttpMessageHandler.class).to(HttpMessageHandlerImpl.class).in(Singleton.class);
    }

}
