package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;

public interface ServerFactory {

    Server create(Configuration configuration);

}
