package io.github.galop_proxy.galop.starter;

import com.google.inject.Injector;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.configuration.ConfigurationFileLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

final class LoaderImpl implements Loader {

    private static final Logger LOGGER = LogManager.getLogger(Loader.class);
    private static final String ARGUMENT_MESSAGE = "Please pass the path to the configuration file as an argument.";

    private final ConfigurationFileLoader configurationFileLoader;
    private final Runtime runtime;
    private final Injector injector;

    @Inject
    LoaderImpl(final ConfigurationFileLoader configurationFileLoader, final Runtime runtime, final Injector injector) {
        this.configurationFileLoader = requireNonNull(configurationFileLoader, "configurationFileLoader must not be null.");
        this.runtime = requireNonNull(runtime, "runtime must not be null.");
        this.injector = requireNonNull(injector, "injector must not be null.");
    }

    @Override
    public void load(final String[] args) {

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

        final Injector childInjector = injector.createChildInjector(new LoadedModule(configuration));
        final Starter starter = childInjector.getInstance(Starter.class);

        starter.start();

    }

}
