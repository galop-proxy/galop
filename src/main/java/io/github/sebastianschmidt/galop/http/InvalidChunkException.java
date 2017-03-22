package io.github.sebastianschmidt.galop.http;

import java.io.IOException;

public class InvalidChunkException extends IOException {

    public InvalidChunkException(final String message) {
        super(message);
    }

    public InvalidChunkException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
