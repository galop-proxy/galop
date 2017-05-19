package io.github.galop_proxy.galop.proxy;

import com.google.common.collect.MapMaker;
import io.github.galop_proxy.galop.http.HttpResponse;
import io.github.galop_proxy.galop.http.HttpStatusCode;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import static io.github.galop_proxy.galop.http.HttpStatusCode.GATEWAY_TIMEOUT;
import static io.github.galop_proxy.galop.http.HttpStatusCode.SERVICE_UNAVAILABLE;
import static java.util.Objects.requireNonNull;

final class ServerImpl implements Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    private final ProxySocketFactory proxySocketFactory;
    private final TargetSocketFactory targetSocketFactory;
    private final ConnectionHandlerFactory connectionHandlerFactory;
    private final ExecutorService executorService;
    private final ConcurrentMap<ConnectionHandler, Long> connectionHandlers;

    private ServerSocket serverSocket;

    @Inject
    ServerImpl(final ProxySocketFactory proxySocketFactory, final TargetSocketFactory targetSocketFactory,
               final ConnectionHandlerFactory connectionHandlerFactory, final ExecutorService executorService) {
        this.proxySocketFactory = requireNonNull(proxySocketFactory, "proxySocketFactory must not be null.");
        this.targetSocketFactory = requireNonNull(targetSocketFactory, "targetSocketFactory must not be null.");
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
                target = targetSocketFactory.create();

                handleNewConnection(source, target);

            } catch (final ConnectException ex) {
                handleServerError(SERVICE_UNAVAILABLE, source);
            } catch (final SocketTimeoutException ex) {
                handleServerError(GATEWAY_TIMEOUT, source);
            } catch (final Exception ex) {
                handleUnexpectedException(ex, source, target);
            }

        }

    }

    private void initServerSocket() {
        try {
            LOGGER.info("Initialize server socket...");
            serverSocket = proxySocketFactory.create();
            LOGGER.info("Server socket initialized.");
        } catch (final IOException ex) {
            throw new RuntimeException("Could not initialize server socket: " + ex.getMessage(), ex);
        }
    }

    private void handleNewConnection(final Socket source, final Socket target) {
        final ConnectionHandler handler = connectionHandlerFactory.create(source, target);
        connectionHandlers.put(handler, System.currentTimeMillis());
        executorService.execute(handler);
    }

    private void handleServerError(final HttpStatusCode httpStatusCode, final Socket source) {
        try {
            final byte[] response = HttpResponse.createWithStatus(httpStatusCode).build();
            IOUtils.write(response, source.getOutputStream());
        } catch (final Exception ex) {
            // Can be ignored.
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    private void handleUnexpectedException(final Exception ex, final Socket source, final Socket target) {

        if (!"socket closed".equals(String.valueOf(ex.getMessage()).toLowerCase())) {
            LOGGER.error("An error occurred while processing a new connection.", ex);
        }

        IOUtils.closeQuietly(source);
        IOUtils.closeQuietly(target);

    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(serverSocket);
        connectionHandlers.keySet().forEach(IOUtils::closeQuietly);
    }

}
