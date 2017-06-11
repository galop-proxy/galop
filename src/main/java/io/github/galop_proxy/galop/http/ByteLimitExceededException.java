package io.github.galop_proxy.galop.http;

import java.io.IOException;

final class ByteLimitExceededException extends IOException {

    ByteLimitExceededException() {
        super("The limit of bytes was exceeded.");
    }

}
