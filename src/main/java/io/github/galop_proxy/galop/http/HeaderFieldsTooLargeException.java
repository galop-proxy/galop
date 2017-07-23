package io.github.galop_proxy.galop.http;

import java.io.IOException;

final class HeaderFieldsTooLargeException extends IOException {

    HeaderFieldsTooLargeException(final String message) {
        super(message);
    }

}
