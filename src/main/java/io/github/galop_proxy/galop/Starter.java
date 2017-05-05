package io.github.galop_proxy.galop;

import io.github.galop_proxy.galop.administration.MonitorFactory;
import io.github.galop_proxy.galop.proxy.Server;
import io.github.galop_proxy.galop.administration.ShutdownHandlerFactory;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.configuration.ConfigurationFileLoader;
import io.github.galop_proxy.galop.proxy.ServerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

final class Starter {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ARGUMENT_MESSAGE = "Please pass the path to the configuration file as an argument.";

    private final Runtime runtime;
    private final ConfigurationFileLoader configurationFileLoader;
    private final MonitorFactory monitorFactory;
    private final ServerFactory serverFactory;
    private final ShutdownHandlerFactory shutdownHandlerFactory;

    @Inject
    Starter(final Runtime runtime, final ConfigurationFileLoader configurationFileLoader,
            final MonitorFactory monitorFactory, final ServerFactory serverFactory,
            final ShutdownHandlerFactory shutdownHandlerFactory) {
        this.runtime = requireNonNull(runtime, "runtime must not be null.");
        this.configurationFileLoader = requireNonNull(configurationFileLoader,
                "configurationFileLoader must not be null.");
        this.monitorFactory = requireNonNull(monitorFactory, "monitorFactory must not be null.");
        this.serverFactory = requireNonNull(serverFactory, "serverFactory must not be null.");
        this.shutdownHandlerFactory = requireNonNull(shutdownHandlerFactory, "shutdownHandlerFactory must not be null.");
    }

    void start(final String[] args) {

        if (args.length != 1) {
            LOGGER.error("No path to configuration file. " + ARGUMENT_MESSAGE);
            runtime.exit(1);
            return;
        }

        if ("-help".equals(args[0]) || "--help".equals(args[0])) {
            LOGGER.info(ARGUMENT_MESSAGE);
            return;
        }

        final Configuration configuration;

        try {
            configuration = configurationFileLoader.load(Paths.get(args[0]));
        } catch (final Exception ex) {
            runtime.exit(1);
            return;
        }

        try {

            final Server server = serverFactory.create(configuration);
            final Thread monitor = monitorFactory.create(configuration);
            final Thread shutdownHandler = shutdownHandlerFactory.create(configuration, server, monitor);

            runtime.addShutdownHook(shutdownHandler);
            monitor.start();
            server.run();

        } catch (final Exception ex) {
            LOGGER.error("An error occurred while starting Galop: " + ex.getMessage());
            runtime.exit(1);
        }

    }

}
