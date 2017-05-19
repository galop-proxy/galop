package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.configuration.HttpConnectionConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.Objects.requireNonNull;

final class MonitorImpl extends Thread implements Monitor {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ThreadPoolExecutor threadPool;
    private final long logInterval;

    @Inject
    MonitorImpl(final HttpConnectionConfiguration configuration, final ExecutorService executorService) {

        requireNonNull(configuration, "configuration must not be null.");
        requireNonNull(executorService, "executorService must not be null.");

        if (!(executorService instanceof ThreadPoolExecutor)) {
            throw new IllegalArgumentException("Not supported ExecutorService. "
                    + "The ExecutorService must be a ThreadPoolExecutor.");
        }

        this.threadPool = (ThreadPoolExecutor) executorService;
        this.logInterval = configuration.getLogInterval();

        setName("MonitorImpl");

    }

    @Override
    public void run() {

        while (!interrupted()) {

            try {
                Thread.sleep(logInterval);
            } catch (final InterruptedException ex) {
                interrupt();
            }

            final int activeConnectionHandlers = (int) Math.ceil(threadPool.getActiveCount() / 2d);
            LOGGER.info("Active connection handlers: " + activeConnectionHandlers);

        }

    }

}
