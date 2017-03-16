package io.github.sebastianschmidt.galop;

import com.google.inject.AbstractModule;
import io.github.sebastianschmidt.galop.commons.CommonsModule;
import io.github.sebastianschmidt.galop.parser.ParserModule;

public final class GalopModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new CommonsModule());
        install(new ParserModule());
    }

}
