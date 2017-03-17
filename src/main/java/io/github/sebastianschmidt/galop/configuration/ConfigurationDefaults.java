package io.github.sebastianschmidt.galop.configuration;

final class ConfigurationDefaults {

    static final int MAX_HTTP_HEADER_SIZE = 8192;

    static final long ACTIVE_CONNECTION_HANDLERS_LOG_INTERVAL = 60000;

    private ConfigurationDefaults() {
        throw new AssertionError("No instances");
    }

}
