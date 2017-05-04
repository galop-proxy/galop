package io.github.sebastianschmidt.galop.configuration;

final class ConfigurationDefaults {

    static final long TARGET_CONNECTION_TIMEOUT = 15000;
    static final int MAX_HTTP_HEADER_SIZE = 8192;
    static final long CONNECTION_HANDLERS_LOG_INTERVAL = 60000;
    static final long CONNECTION_HANDLERS_TERMINATION_TIMEOUT = 30000;

    private ConfigurationDefaults() {
        throw new AssertionError("No instances");
    }

}
