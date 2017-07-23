package io.github.galop_proxy.galop.http;

import java.io.IOException;

final class LineTooLargeException extends IOException {

    LineTooLargeException(final int sizeLimit) {
        super("Invalid line: Size limit exceeded: " + sizeLimit);
    }

}

