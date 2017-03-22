package io.github.sebastianschmidt.galop.http;

import java.nio.charset.Charset;

public class HttpConstants {

    public static final String HTTP_VERSION = "HTTP/1.1";

    public static final Charset HEADER_CHARSET = Charset.forName("ASCII");

    public static final String HEADER_HOST_PREFIX = "Host:".toLowerCase();
    public static final String HEADER_DATE_PREFIX = "Date:".toLowerCase();
    public static final String HEADER_SERVER_PREFIX = "Server:".toLowerCase();
    public static final String HEADER_CONTENT_LENGTH_PREFIX = "Content-Length:".toLowerCase();
    public static final String HEADER_CONTENT_TYPE_PREFIX = "Content-Type:".toLowerCase();
    public static final String HEADER_CONTENT_TYPE_CHARSET_PREFIX = "charset=";
    public static final String HEADER_TRANSFER_ENCODING_PREFIX = "Transfer-Encoding:".toLowerCase();

    public static final String TRANSFER_ENCODING_IDENTITY = "identity";
    public static final String TRANSFER_ENCODING_CHUNKED = "chunked";

    public static final String NEW_LINE = "\r\n";
    public static final char SPACE = ' ';
    public static final char VALUE_SEPARATOR = ';';

    private HttpConstants() {
        throw new AssertionError("No instances");
    }

}
