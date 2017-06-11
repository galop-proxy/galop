package io.github.galop_proxy.galop.starter;

import io.github.galop_proxy.galop.administration.Monitor;
import io.github.galop_proxy.galop.administration.ShutdownHandler;
import io.github.galop_proxy.galop.proxy.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class StarterImpl implements Starter {

    private static final Logger LOGGER = LogManager.getLogger(Starter.class);

    private final Runtime runtime;
    private final ShutdownHandler shutdownHandler;
    private final Monitor monitor;
    private final Server server;

    @Inject
    StarterImpl(final Runtime runtime, final ShutdownHandler shutdownHandler, final Monitor monitor,
                final Server server) {
        this.runtime = checkNotNull(runtime, "runtime");
        this.shutdownHandler = checkNotNull(shutdownHandler, "shutdownHandler");
        this.monitor = checkNotNull(monitor, "monitor");
        this.server = checkNotNull(server, "server");
    }

    @Override
    public void start() {

        try {
            runtime.addShutdownHook(new Thread(shutdownHandler));
            monitor.start();
            server.run();
        } catch (final Exception ex) {
            LOGGER.error("An error occurred while starting Galop: " + ex.getMessage());
            runtime.exit(1);
        }

    }

}
