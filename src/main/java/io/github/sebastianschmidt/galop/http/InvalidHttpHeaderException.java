package io.github.sebastianschmidt.galop.http;

import java.io.IOException;

public class InvalidHttpHeaderException extends IOException {

    public InvalidHttpHeaderException(final String message) {
        super(message);
    }

}
