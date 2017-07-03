package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.ExchangeHandler;

import javax.inject.Inject;
import java.net.Socket;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class ConnectionHandlerFactoryImpl implements ConnectionHandlerFactory {

    private final ExchangeHandler exchangeHandler;

    @Inject
    ConnectionHandlerFactoryImpl(final ExchangeHandler exchangeHandler) {
        this.exchangeHandler = checkNotNull(exchangeHandler, "exchangeHandler");
    }

    @Override
    public ConnectionHandler create(final Socket source, final Socket target) {
        return new ConnectionHandlerImpl(exchangeHandler, source, target);
    }

}
