package io.github.galop_proxy.galop.http;

import java.io.IOException;

public class InvalidHttpHeaderException extends IOException {

    public InvalidHttpHeaderException(final String message) {
        super(message);
    }

}
