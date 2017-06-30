package io.github.galop_proxy.galop.http;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class HttpModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MessageParser.class).to(MessageParserImpl.class).in(Singleton.class);
        bind(HttpHeaderParser.class).to(HttpHeaderParserImpl.class).in(Singleton.class);
        bind(HttpMessageHandler.class).to(HttpMessageHandlerImpl.class).in(Singleton.class);
        bind(HttpExchangeHandler.class).to(HttpExchangeHandlerImpl.class).in(Singleton.class);
    }

}
