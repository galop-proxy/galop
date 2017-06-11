package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.configuration.HttpConnectionConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class MonitorImpl extends Thread implements Monitor {

    private static final Logger LOGGER = LogManager.getLogger(Monitor.class);

    private final ThreadPoolExecutor threadPool;
    private final long logInterval;

    @Inject
    MonitorImpl(final HttpConnectionConfiguration configuration, final ExecutorService executorService) {

        checkNotNull(configuration, "configuration");
        checkNotNull(executorService, "executorService");

        if (!(executorService instanceof ThreadPoolExecutor)) {
            throw new IllegalArgumentException("Not supported ExecutorService. "
                    + "The ExecutorService must be a ThreadPoolExecutor.");
        }

        this.threadPool = (ThreadPoolExecutor) executorService;
        this.logInterval = configuration.getLogInterval();

        setName("Monitor");

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
