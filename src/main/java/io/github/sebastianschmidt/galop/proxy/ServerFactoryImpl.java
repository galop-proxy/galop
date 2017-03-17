package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.commons.ServerSocketFactory;
import io.github.sebastianschmidt.galop.commons.SocketFactory;
import io.github.sebastianschmidt.galop.configuration.Configuration;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

final class ServerFactoryImpl implements ServerFactory {

    private final ServerSocketFactory serverSocketFactory;
    private final SocketFactory socketFactory;
    private final ConnectionHandlerFactory connectionHandlerFactory;
    private final ExecutorService executorService;

    @Inject
    ServerFactoryImpl(final ServerSocketFactory serverSocketFactory, final SocketFactory socketFactory,
                      final ConnectionHandlerFactory connectionHandlerFactory, final ExecutorService executorService) {
        this.serverSocketFactory = requireNonNull(serverSocketFactory, "serverSocketFactory must not be null.");
        this.socketFactory = requireNonNull(socketFactory, "socketFactory must not be null.");
        this.connectionHandlerFactory = requireNonNull(connectionHandlerFactory,
                "connectionHandlerFactory must not be null.");
        this.executorService = requireNonNull(executorService, "executorService must not be null.");
    }

    @Override
    public Server create(final Configuration configuration) {
        return new ServerImpl(configuration, serverSocketFactory, socketFactory, connectionHandlerFactory, executorService);
    }

}
