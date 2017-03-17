package io.github.sebastianschmidt.galop.administration;

import io.github.sebastianschmidt.galop.configuration.Configuration;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

final class MonitorFactoryImpl implements MonitorFactory {

    private final ExecutorService executorService;

    @Inject
    MonitorFactoryImpl(final ExecutorService executorService) {
        this.executorService = requireNonNull(executorService, "executorService must not be null.");
    }

    @Override
    public Thread create(final Configuration configuration) {
        return new Monitor(configuration, executorService);
    }

}
