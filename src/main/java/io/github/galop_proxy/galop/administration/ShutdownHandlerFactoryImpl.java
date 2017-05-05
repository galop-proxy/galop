package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.proxy.Server;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

final class ShutdownHandlerFactoryImpl implements ShutdownHandlerFactory {

    private final ExecutorService executorService;

    @Inject
    ShutdownHandlerFactoryImpl(final ExecutorService executorService) {
        this.executorService = requireNonNull(executorService, "executorService must not be null.");
    }

    @Override
    public Thread create(final Configuration configuration, final Server server, final Thread monitor) {
        return new ShutdownHandler(configuration, server, executorService, monitor);
    }

}
