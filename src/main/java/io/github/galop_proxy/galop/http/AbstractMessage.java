package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

abstract class AbstractMessage implements Message {

    private Version version;

    private final Map<String, List<String>> headers;

    AbstractMessage(final Version version) {
        setVersion(version);
        headers = new HashMap<>();
    }

    @Override
    public final Version getVersion() {
        return version;
    }

    @Override
    public final void setVersion(final Version version) {
        this.version = checkNotNull(version, "version");
    }

    @Override
    public final Map<String, List<String>> getHeaders() {
        return headers;
    }

}
