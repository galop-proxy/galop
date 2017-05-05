package io.github.galop_proxy.galop.http;

import java.io.IOException;

class InvalidChunkException extends IOException {

    InvalidChunkException(final String message) {
        super(message);
    }

    InvalidChunkException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
