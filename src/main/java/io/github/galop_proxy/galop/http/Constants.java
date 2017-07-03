package io.github.galop_proxy.galop.http;

import com.google.common.collect.Range;

import java.nio.charset.Charset;

final class Constants {

    static final String HTTP_VERSION_PREFIX = "HTTP/";
    static final String HTTP_VERSION = HTTP_VERSION_PREFIX + "1.1";

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

    static final Range<Integer> STATUS_CODE_RANGE = Range.closed(0, 999);

    private Constants() {
        throw new AssertionError("No instances");
    }

}
