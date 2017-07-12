package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Version;

import java.io.IOException;
import java.util.Objects;

final class UnsupportedHttpVersionException extends IOException {

    UnsupportedHttpVersionException(final Version unsupportedVersion) {
        super("Unsupported HTTP version: " + Objects.toString(unsupportedVersion));
    }

}
