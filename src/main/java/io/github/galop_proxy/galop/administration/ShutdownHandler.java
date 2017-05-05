package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.proxy.Server;
import io.github.galop_proxy.galop.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

final class ShutdownHandler extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Configuration configuration;
    private final Server server;
    private final ExecutorService executorService;
    private final Thread monitor;

    ShutdownHandler(final Configuration configuration, final Server server, final ExecutorService executorService,
                    final Thread monitor) {
        this.configuration = requireNonNull(configuration, "configuration must not be null.");
        this.server = requireNonNull(server, "server must not be null.");
        this.executorService = requireNonNull(executorService, "executorService must not be null.");
        this.monitor = requireNonNull(monitor, "monitor must not be null.");
    }

    @Override
    public void run() {
        terminateServerAndConnectionHandlers();
        waitForConnectionHandlersToTerminate();
        terminateMonitor();
        terminateLogging();
    }

    private void terminateServerAndConnectionHandlers() {
        try {
            LOGGER.info("Terminate server and notify connection handlers to terminate as soon as possible...");
            server.close();
            LOGGER.info("Server terminated.");
        } catch (final Exception ex) {
            logError("An error occurred while terminating the server.", ex);
        }
    }

    private void waitForConnectionHandlersToTerminate() {
        try {

            final long terminationTimeout = configuration.getConnectionHandlersTerminationTimeout();
            LOGGER.info("Wait for the connection handlers to terminate... (Timeout: " + terminationTimeout + ")");

            executorService.shutdownNow();

            final boolean timeout = !executorService.awaitTermination(terminationTimeout, TimeUnit.MILLISECONDS);

            if (!timeout) {
                LOGGER.info("All connection handlers gracefully terminated.");
            } else {
                LOGGER.warn("Timeout while waiting for connection handlers to terminate. "
                        + "Not all connection handlers could be gracefully terminated.");
            }

        } catch (final Exception ex) {
            logError("An error occurred while terminating the connection handlers.", ex);
        }
    }

    private void terminateMonitor() {
        try {
            LOGGER.info("Interrupt monitor...");
            monitor.interrupt();
            LOGGER.info("Monitor interrupted.");
        } catch (final Exception ex) {
            logError("An error occurred while interrupting the monitor.", ex);
        }
    }

    private void logError(final String message, final Exception cause) {
        LOGGER.error(message, cause);
    }

    private void terminateLogging() {
        if (LogManager.getContext() instanceof LoggerContext) {
            Configurator.shutdown((LoggerContext) LogManager.getContext());
        }
    }

}
