package io.github.sebastianschmidt.galop;

import java.io.IOException;

public class InvalidHttpHeaderException extends IOException {

    public InvalidHttpHeaderException(final String message) {
        super(message);
    }

}
