package io.github.galop_proxy.galop.configuration;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;

final class HttpConfigurationImpl implements HttpConfiguration {

    private final HttpConnectionConfiguration connection;
    private final HttpHeaderConfiguration header;

    HttpConfigurationImpl(final HttpConnectionConfiguration connection,  final HttpHeaderConfiguration header) {
        this.connection = checkNotNull(connection, "connection");
        this.header = checkNotNull(header, "header");
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
