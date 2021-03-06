package io.github.galop_proxy.galop.configuration;

import java.net.InetAddress;

final class ConfigurationDefaults {

    static final int PROXY_BACKLOG_SIZE = 50;
    static final InetAddress PROXY_BIND_ADDRESS = null;

    static final int TARGET_CONNECTION_TIMEOUT = 15000;

    static final long HTTP_CONNECTION_LOG_INTERVAL = 60000;
    static final long HTTP_CONNECTION_TERMINATION_TIMEOUT = 30000;

    static final long HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT = 60000;
    static final int HTTP_HEADER_REQUEST_REQUEST_LINE_SIZE_LIMIT = 8192;
    static final int HTTP_HEADER_REQUEST_FIELDS_LIMIT = 100;
    static final int HTTP_HEADER_REQUEST_FIELD_SIZE_LIMIT = 8192;

    static final long HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT = 90000;
    static final int HTTP_HEADER_RESPONSE_STATUS_LINE_SIZE_LIMIT = 8192;
    static final int HTTP_HEADER_RESPONSE_FIELDS_LIMIT = 100;
    static final int HTTP_HEADER_RESPONSE_FIELD_SIZE_LIMIT = 8192;

    private ConfigurationDefaults() {
        throw new AssertionError("No instances");
    }

}
