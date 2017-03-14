package io.github.sebastianschmidt.galop;

import java.io.IOException;

/**
 *
 */
public class ByteLimitExceededException extends IOException {

    public ByteLimitExceededException() {
        super("The limit of bytes was exceeded.");
    }

}
