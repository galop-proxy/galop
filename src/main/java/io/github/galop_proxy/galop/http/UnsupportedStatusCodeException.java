package io.github.galop_proxy.galop.http;

import java.io.IOException;

final class UnsupportedStatusCodeException extends IOException {

    UnsupportedStatusCodeException(final int unsupportedStatusCode) {
        super("Unsupported status code: " + unsupportedStatusCode);
    }

}
