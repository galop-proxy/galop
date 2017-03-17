package io.github.sebastianschmidt.galop.administration;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.proxy.Server;

public interface ShutdownHandlerFactory {

    Thread create(Configuration configuration, Server server, Thread monitor);

}
