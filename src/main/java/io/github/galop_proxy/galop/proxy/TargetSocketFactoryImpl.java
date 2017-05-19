package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.configuration.TargetConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

final class TargetSocketFactoryImpl implements TargetSocketFactory {

    private final TargetConfiguration configuration;

    @Inject
    TargetSocketFactoryImpl(final TargetConfiguration configuration) {
        this.configuration = requireNonNull(configuration, "configuration must not be null.");
    }

    @Override
    public Socket create() throws IOException {

        final InetAddress address = configuration.getAddress();
        final int port = configuration.getPort().getValue();
        final int timeout = configuration.getConnectionTimeout();

        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress(address, port), timeout);
        return socket;

    }

}
