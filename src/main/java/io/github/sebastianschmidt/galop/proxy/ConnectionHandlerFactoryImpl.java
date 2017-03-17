package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.parser.HttpHeaderParser;

import javax.inject.Inject;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

final class ConnectionHandlerFactoryImpl implements ConnectionHandlerFactory {

    private final HttpHeaderParser httpHeaderParser;

    @Inject
    ConnectionHandlerFactoryImpl(final HttpHeaderParser httpHeaderParser) {
        this.httpHeaderParser = requireNonNull(httpHeaderParser, "httpHeaderParser must not be null.");
    }

    @Override
    public Runnable create(final Configuration configuration, final Socket source, final Socket target) {
        return new ConnectionHandler(configuration, httpHeaderParser, source, target);
    }

}
