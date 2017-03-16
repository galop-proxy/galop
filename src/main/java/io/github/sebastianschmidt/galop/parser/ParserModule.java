package io.github.sebastianschmidt.galop.parser;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public final class ParserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HttpHeaderParser.class).to(HttpHeaderParserImpl.class).in(Singleton.class);
    }

}
