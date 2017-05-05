package io.github.galop_proxy.galop.configuration;

final class ConfigurationPropertyKeys {

    static final String PROXY_PORT = "proxy_port";
    static final String TARGET_ADDRESS = "target.address";
    static final String TARGET_PORT = "target.port";
    static final String TARGET_CONNECTION_TIMEOUT = "target.connection_timeout";
    static final String MAX_HTTP_HEADER_SIZE = "security.max_http_header_size";
    static final String CONNECTION_HANDLERS_LOG_INTERVAL = "connection_handlers.log_interval";
    static final String CONNECTION_HANDLERS_TERMINATION_TIMEOUT = "connection_handlers.termination_timeout";

    private ConfigurationPropertyKeys() {
        throw new AssertionError("No instances");
    }

}
