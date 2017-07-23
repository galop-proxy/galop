package io.github.galop_proxy.galop.configuration;

final class ConfigurationPropertyKeys {

    static final String PROXY_PORT = "proxy.port";
    static final String PROXY_BACKLOG_SIZE = "proxy.backlog_size";
    static final String PROXY_BIND_ADDRESS = "proxy.bind_address";

    static final String TARGET_ADDRESS = "target.address";
    static final String TARGET_PORT = "target.port";
    static final String TARGET_CONNECTION_TIMEOUT = "target.connection_timeout";

    static final String HTTP_CONNECTION_LOG_INTERVAL = "http.connection.log_interval";
    static final String HTTP_CONNECTION_TERMINATION_TIMEOUT = "http.connection.termination_timeout";

    static final String HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT = "http.header.request.receive_timeout";
    static final String HTTP_HEADER_REQUEST_REQUEST_LINE_SIZE_LIMIT = "http.header.request.request_line_size_limit";
    static final String HTTP_HEADER_REQUEST_FIELDS_LIMIT = "http.header.request.fields_limit";
    static final String HTTP_HEADER_REQUEST_FIELD_SIZE_LIMIT = "http.header.request.field_size_limit";

    static final String HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT = "http.header.response.receive_timeout";
    static final String HTTP_HEADER_RESPONSE_STATUS_LINE_SIZE_LIMIT = "http.header.response.status_line_size_limit";
    static final String HTTP_HEADER_RESPONSE_FIELDS_LIMIT = "http.header.response.fields_limit";
    static final String HTTP_HEADER_RESPONSE_FIELD_SIZE_LIMIT = "http.header.response.field_size_limit";

    private ConfigurationPropertyKeys() {
        throw new AssertionError("No instances");
    }

}
