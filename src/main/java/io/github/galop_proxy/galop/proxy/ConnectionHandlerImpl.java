package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class ConnectionHandlerImpl implements ConnectionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionHandler.class);

    private final ExchangeHandler exchangeHandler;
    private final Socket source;
    private final Socket target;

    ConnectionHandlerImpl(final ExchangeHandler exchangeHandler, final Socket source, final Socket target) {
        this.exchangeHandler = checkNotNull(exchangeHandler, "exchangeHandler");
        this.source = checkNotNull(source, "source");
        this.target = checkNotNull(target, "target");
    }

    @Override
    public void run() {

        try {
            exchangeHandler.handleRequest(source, target);
            exchangeHandler.handleResponse(source, target);
            LOGGER.info("FINISHED!");
        } catch (final Exception ex) {
            LOGGER.debug(ex);
        } finally {
            IOUtils.closeQuietly(source);
            IOUtils.closeQuietly(target);
        }

    }

    @Override
    public void close() throws IOException {
        // Nothing to do.
    }

}
