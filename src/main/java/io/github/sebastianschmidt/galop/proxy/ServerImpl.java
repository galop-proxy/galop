package io.github.sebastianschmidt.galop.proxy;

import com.google.common.collect.MapMaker;
import io.github.sebastianschmidt.galop.commons.ServerSocketFactory;
import io.github.sebastianschmidt.galop.commons.SocketFactory;
import io.github.sebastianschmidt.galop.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

final class ServerImpl implements Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    private final Configuration config;
    private final ServerSocketFactory serverSocketFactory;
    private final SocketFactory socketFactory;
    private final ConnectionHandlerFactory connectionHandlerFactory;
    private final ExecutorService executorService;
    private final ConcurrentMap<ConnectionHandler, Long> connectionHandlers;

    private ServerSocket serverSocket;

    ServerImpl(final Configuration configuration, final ServerSocketFactory serverSocketFactory,
               final SocketFactory socketFactory, final ConnectionHandlerFactory connectionHandlerFactory,
               final ExecutorService executorService) {
        this.config = requireNonNull(configuration, "configuration must not be null.");
        this.serverSocketFactory = requireNonNull(serverSocketFactory, "serverSocketFactory must not be null.");
        this.socketFactory = requireNonNull(socketFactory, "socketFactory must not be null.");
        this.connectionHandlerFactory = requireNonNull(connectionHandlerFactory,
                "connectionHandlerFactory must not be null.");
        this.executorService = requireNonNull(executorService, "executorService must not be null.");
        this.connectionHandlers = new MapMaker().weakKeys().makeMap();
    }

    @Override
    public void run() {

        initServerSocket();

        while (!serverSocket.isClosed()) {

            Socket source = null;
            Socket target = null;

            try {

                source = serverSocket.accept();
                target = socketFactory.create(config.getTargetAddress(), config.getTargetPort().getValue());

                handleNewConnection(source, target);

            } catch (final Exception ex) {

                if (!"socket closed".equals(String.valueOf(ex.getMessage()).toLowerCase())) {
                    LOGGER.error("An error occurred while processing a new connection.", ex);
                }

                IOUtils.closeQuietly(source);
                IOUtils.closeQuietly(target);

            }

        }

    }

    private void initServerSocket() {
        try {
            serverSocket = serverSocketFactory.create(config.getProxyPort().getValue());
        } catch (final IOException ex) {
            throw new RuntimeException("Could not create server socket: " + ex.getMessage(), ex);
        }
    }

    private void handleNewConnection(final Socket source, final Socket target) {
        final ConnectionHandler handler = connectionHandlerFactory.create(config, source, target);
        connectionHandlers.put(handler, System.currentTimeMillis());
        executorService.execute(handler);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(serverSocket);
        connectionHandlers.keySet().forEach(IOUtils::closeQuietly);
    }

}
