package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.HttpExchangeHandler;

import javax.inject.Inject;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

final class ConnectionHandlerFactoryImpl implements ConnectionHandlerFactory {

    private final HttpExchangeHandler httpExchangeHandler;

    @Inject
    ConnectionHandlerFactoryImpl(final HttpExchangeHandler httpExchangeHandler) {
        this.httpExchangeHandler = requireNonNull(httpExchangeHandler, "httpExchangeHandler must not be null.");
    }

    @Override
    public ConnectionHandler create(final Socket source, final Socket target) {
        return new ConnectionHandlerImpl(httpExchangeHandler, source, target);
    }

}
