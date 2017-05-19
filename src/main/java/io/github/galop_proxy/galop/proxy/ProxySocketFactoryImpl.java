package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.configuration.ProxyConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.ServerSocket;

import static java.util.Objects.requireNonNull;

final class ProxySocketFactoryImpl implements ProxySocketFactory {

    private static final Logger LOGGER = LogManager.getLogger(ProxySocketFactory.class);

    private final ProxyConfiguration configuration;

    @Inject
    ProxySocketFactoryImpl(final ProxyConfiguration configuration) {
        this.configuration = requireNonNull(configuration, "configuration must not be null.");
    }

    @Override
    public ServerSocket create() throws IOException {
        return new ServerSocket(configuration.getPort().getValue());
    }

}
