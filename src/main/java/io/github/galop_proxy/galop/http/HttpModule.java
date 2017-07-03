package io.github.galop_proxy.galop.http;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class HttpModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(ExchangeHandler.class).to(ExchangeHandlerImpl.class).in(Singleton.class);

        bind(MessageParser.class).to(MessageParserImpl.class).in(Singleton.class);
        bind(StartLineParser.class).to(StartLineParserImpl.class).in(Singleton.class);
        bind(HeaderParser.class).to(HeaderParserImpl.class).in(Singleton.class);

        bind(MessageWriter.class).to(MessageWriterImpl.class).in(Singleton.class);
        bind(StartLineWriter.class).to(StartLineWriterImpl.class).in(Singleton.class);

        bind(HttpHeaderParser.class).to(HttpHeaderParserImpl.class).in(Singleton.class);

    }

}
