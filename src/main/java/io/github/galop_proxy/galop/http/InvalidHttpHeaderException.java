package io.github.galop_proxy.galop.http;

import java.io.IOException;

final class InvalidHttpHeaderException extends IOException {

    InvalidHttpHeaderException(final String message) {
        super(message);
    }

}
