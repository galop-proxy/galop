package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.HttpHeaderParser;
import io.github.galop_proxy.galop.http.HttpMessageHandler;
import io.github.galop_proxy.galop.configuration.Configuration;

import javax.inject.Inject;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

final class ConnectionHandlerFactoryImpl implements ConnectionHandlerFactory {

    private final HttpHeaderParser httpHeaderParser;
    private final HttpMessageHandler httpMessageHandler;

    @Inject
    ConnectionHandlerFactoryImpl(final HttpHeaderParser httpHeaderParser, final HttpMessageHandler httpMessageHandler) {
        this.httpHeaderParser = requireNonNull(httpHeaderParser, "httpHeaderParser must not be null.");
        this.httpMessageHandler = requireNonNull(httpMessageHandler, "httpMessageHandler must not be null.");
    }

    @Override
    public ConnectionHandler create(final Configuration configuration, final Socket source, final Socket target) {
        return new ConnectionHandlerImpl(configuration, httpHeaderParser, httpMessageHandler, source, target);
    }

}
