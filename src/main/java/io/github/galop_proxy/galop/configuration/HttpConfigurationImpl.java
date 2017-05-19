package io.github.galop_proxy.galop.configuration;

import static java.util.Objects.requireNonNull;

final class HttpConfigurationImpl implements HttpConfiguration {

    private final HttpConnectionConfiguration connection;
    private final HttpHeaderConfiguration header;

    HttpConfigurationImpl(final HttpConnectionConfiguration connection,  final HttpHeaderConfiguration header) {
        this.connection = requireNonNull(connection, "connection must not be null.");
        this.header = requireNonNull(header, "header must not be null.");
    }

    @Override
    public HttpConnectionConfiguration getConnection() {
        return connection;
    }

    @Override
    public HttpHeaderConfiguration getHeader() {
        return header;
    }

}
