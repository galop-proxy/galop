package io.github.sebastianschmidt.galop.configuration;

final class ConfigurationPropertyKeys {

    static final String PROXY_PORT = "proxy_port";
    static final String TARGET_ADDRESS = "target_address";
    static final String TARGET_PORT = "target_port";
    static final String MAX_HTTP_HEADER_SIZE = "max_http_header_size";

    private ConfigurationPropertyKeys() {
        throw new AssertionError("No instances");
    }

}
