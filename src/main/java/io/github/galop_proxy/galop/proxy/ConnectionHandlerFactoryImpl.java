package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.HttpExchangeHandler;

import javax.inject.Inject;
import java.net.Socket;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class ConnectionHandlerFactoryImpl implements ConnectionHandlerFactory {

    private final HttpExchangeHandler httpExchangeHandler;

    @Inject
    ConnectionHandlerFactoryImpl(final HttpExchangeHandler httpExchangeHandler) {
        this.httpExchangeHandler = checkNotNull(httpExchangeHandler, "httpExchangeHandler");
    }

    @Override
    public ConnectionHandler create(final Socket source, final Socket target) {
        return new ConnectionHandlerImpl(httpExchangeHandler, source, target);
    }

}
