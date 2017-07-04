package io.github.galop_proxy.galop.http;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class HttpModule extends AbstractModule {

    @Override
    protected void configure() {
        bindHandler();
        bindParsers();
        bindWriters();
    }

    private void bindHandler() {
        bind(ExchangeHandler.class).to(ExchangeHandlerImpl.class).in(Singleton.class);
    }

    private void bindParsers() {
        bind(MessageParser.class).to(MessageParserImpl.class).in(Singleton.class);
        bind(StartLineParser.class).to(StartLineParserImpl.class).in(Singleton.class);
        bind(HeaderParser.class).to(HeaderParserImpl.class).in(Singleton.class);
    }

    private void bindWriters() {
        bind(MessageWriter.class).to(MessageWriterImpl.class).in(Singleton.class);
        bind(StartLineWriter.class).to(StartLineWriterImpl.class).in(Singleton.class);
        bind(HeaderWriter.class).to(HeaderWriterImpl.class).in(Singleton.class);
        bind(MessageBodyWriter.class).to(MessageBodyWriterImpl.class).in(Singleton.class);
    }

}
