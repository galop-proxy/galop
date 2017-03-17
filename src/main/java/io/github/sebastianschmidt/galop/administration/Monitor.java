package io.github.sebastianschmidt.galop.administration;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.Objects.requireNonNull;

final class Monitor extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ThreadPoolExecutor threadPool;
    private final long logInterval;

    Monitor(final Configuration configuration, final ExecutorService executorService) {

        requireNonNull(configuration, "configuration must not be null.");
        requireNonNull(executorService, "executorService must not be null.");

        if (!(executorService instanceof ThreadPoolExecutor)) {
            throw new IllegalArgumentException("Not supported ExecutorService. "
                    + "The ExecutorService must be a ThreadPoolExecutor.");
        }

        this.threadPool = (ThreadPoolExecutor) executorService;
        this.logInterval = configuration.getConnectionHandlersLogInterval();

        setName("Monitor");

    }

    @Override
    public void run() {

        while (!interrupted()) {

            LOGGER.info("Active connection handlers: " + threadPool.getActiveCount());

            try {
                Thread.sleep(logInterval);
            } catch (final InterruptedException ex) {
                interrupt();
            }

        }

    }

}
