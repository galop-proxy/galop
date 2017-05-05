package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.proxy.Server;

public interface ShutdownHandlerFactory {

    Thread create(Configuration configuration, Server server, Thread monitor);

}
