package io.github.galop_proxy.galop.http;

import java.nio.charset.Charset;

class HttpConstants {

    static final String HTTP_VERSION = "HTTP/1.1";

    static final Charset HEADER_CHARSET = Charset.forName("ASCII");

    static final String HEADER_HOST_PREFIX = "host:";
    static final String HEADER_DATE_PREFIX = "date:";
    static final String HEADER_SERVER_PREFIX = "server:";
    static final String HEADER_CONTENT_LENGTH_PREFIX = "content-length:";
    static final String HEADER_CONTENT_TYPE_PREFIX = "content-type:";
    static final String HEADER_CONTENT_TYPE_CHARSET_PREFIX = "charset=";
    static final String HEADER_TRANSFER_ENCODING_PREFIX = "transfer-encoding:";

    static final String TRANSFER_ENCODING_IDENTITY = "identity";
    static final String TRANSFER_ENCODING_CHUNKED = "chunked";

    static final String NEW_LINE = "\r\n";
    static final char SPACE = ' ';
    static final char VALUE_SEPARATOR = ';';

    private HttpConstants() {
        throw new AssertionError("No instances");
    }

}
