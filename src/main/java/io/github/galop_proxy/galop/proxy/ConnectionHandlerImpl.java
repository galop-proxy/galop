package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.Socket;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class ConnectionHandlerImpl implements ConnectionHandler {

    private final HttpExchangeHandler httpExchangeHandler;
    private final Socket source;
    private final Socket target;

    private volatile boolean currentlyHandlingRequestOrResponse;
    private volatile boolean connectionShouldBeClosed;

    ConnectionHandlerImpl(final HttpExchangeHandler httpExchangeHandler, final Socket source, final Socket target) {
        this.httpExchangeHandler = checkNotNull(httpExchangeHandler, "httpExchangeHandler");
        this.source = checkNotNull(source, "source");
        this.target = checkNotNull(target, "target");
    }

    @Override
    public void run() {

        try {

            while (!source.isClosed() && !target.isClosed() && !connectionShouldBeClosed && !Thread.interrupted()) {
                handleRequest();
                handleResponse();
            }

        } catch (final Exception ex) {
            // Can be ignored.
        } finally {
            closeConnection();
        }

    }

    private void handleRequest() throws Exception {
        httpExchangeHandler.handleRequest(source, target, this::markStartHandlingRequest);
    }

    private void handleResponse() throws Exception {
        httpExchangeHandler.handleResponse(source, target, this::markEndHandlingResponse);
    }

    private synchronized void markStartHandlingRequest() {
        currentlyHandlingRequestOrResponse = true;
    }

    private synchronized void markEndHandlingResponse() {
        currentlyHandlingRequestOrResponse = false;
        checkIfConnectionShouldAndCanBeClosed();
    }

    private synchronized void markConnectionShouldBeClosed() {
        connectionShouldBeClosed = true;
        checkIfConnectionShouldAndCanBeClosed();
    }

    private void checkIfConnectionShouldAndCanBeClosed() {
        if (connectionShouldBeClosed && !currentlyHandlingRequestOrResponse) {
            closeConnection();
        }
    }

    private void closeConnection() {
        IOUtils.closeQuietly(source);
        IOUtils.closeQuietly(target);
    }

    @Override
    public void close() throws IOException {
        markConnectionShouldBeClosed();
    }

}
