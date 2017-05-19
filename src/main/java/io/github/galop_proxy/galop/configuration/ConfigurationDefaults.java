package io.github.galop_proxy.galop.configuration;

final class ConfigurationDefaults {

    static final int TARGET_CONNECTION_TIMEOUT = 15000;

    static final long HTTP_CONNECTION_LOG_INTERVAL = 60000;
    static final long HTTP_CONNECTION_TERMINATION_TIMEOUT = 30000;

    static final long HTTP_HEADER_REQUEST_RECEIVE_TIMEOUT = 60000;
    static final int HTTP_HEADER_REQUEST_MAX_SIZE = 8192;
    static final long HTTP_HEADER_RESPONSE_RECEIVE_TIMEOUT = 90000;
    static final int HTTP_HEADER_RESPONSE_MAX_SIZE = 8192;

    private ConfigurationDefaults() {
        throw new AssertionError("No instances");
    }

}
