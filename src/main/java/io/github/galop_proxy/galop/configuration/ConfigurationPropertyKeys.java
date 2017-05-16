package io.github.galop_proxy.galop.configuration;

final class ConfigurationPropertyKeys {

    static final String PROXY_PORT = "proxy.port";

    static final String TARGET_ADDRESS = "target.address";
    static final String TARGET_PORT = "target.port";
    static final String TARGET_CONNECTION_TIMEOUT = "target.connection_timeout";

    static final String HTTP_CONNECTION_LOG_INTERVAL = "http.connection.log_interval";
    static final String HTTP_CONNECTION_TERMINATION_TIMEOUT = "http.connection.termination_timeout";

    static final String HTTP_REQUEST_HEADER_RECEIVE_TIMEOUT = "http.request.header.receive_timeout";
    static final String HTTP_RESPONSE_HEADER_RECEIVE_TIMEOUT = "http.response.header.receive_timeout";

    static final String HTTP_HEADER_MAX_SIZE = "security.max_http_header_size";


    private ConfigurationPropertyKeys() {
        throw new AssertionError("No instances");
    }

}
