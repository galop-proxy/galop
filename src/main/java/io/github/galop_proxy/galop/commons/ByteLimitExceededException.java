package io.github.galop_proxy.galop.commons;

import java.io.IOException;

public class ByteLimitExceededException extends IOException {

    public ByteLimitExceededException() {
        super("The limit of bytes was exceeded.");
    }

}
