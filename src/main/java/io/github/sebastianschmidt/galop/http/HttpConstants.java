package io.github.sebastianschmidt.galop.http;

import java.nio.charset.Charset;

public class HttpConstants {

    public static final Charset HTTP_HEADER_CHARSET = Charset.forName("ASCII");

    private HttpConstants() {
        throw new AssertionError("No instances");
    }

}
