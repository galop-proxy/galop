package io.github.sebastianschmidt.galop.parser;

import java.io.IOException;

public class InvalidHttpHeaderException extends IOException {

    public InvalidHttpHeaderException(final String message) {
        super(message);
    }

}
