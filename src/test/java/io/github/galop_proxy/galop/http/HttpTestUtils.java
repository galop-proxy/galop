package io.github.galop_proxy.galop.http;

import static io.github.galop_proxy.galop.http.HttpConstants.*;

public class HttpTestUtils {

    public static String createGetRequest() {
        return "GET /hello-world.html " + HTTP_VERSION + NEW_LINE
                + HEADER_HOST_PREFIX + SPACE + "www.example.com" + NEW_LINE
                + NEW_LINE;
    }

    public static String createResponse(final String content) {
        return createResponse(content, content.getBytes().length + "");
    }

    public static String createResponse(final String content, final String contentLength) {
        return createResponse(content, contentLength, null);
    }

    public static String createResponse(final String content, final String contentLength,
                                        final String transferEncoding) {

        final StringBuilder response  = new StringBuilder();
        response.append(HTTP_VERSION).append(SPACE).append("200 OK").append(NEW_LINE);
        response.append(HEADER_SERVER_PREFIX).append(SPACE).append("Test/1.0").append(NEW_LINE);
        response.append(HEADER_CONTENT_TYPE_PREFIX).append(SPACE).append("text/html").append(NEW_LINE);

        if (contentLength != null) {
            appendHeader(HEADER_CONTENT_LENGTH_PREFIX, contentLength, response);
        }

        if (transferEncoding != null) {
            appendHeader(HEADER_TRANSFER_ENCODING_PREFIX, transferEncoding, response);
        }

        response.append(NEW_LINE);
        response.append(content);

        return response.toString();

    }

    private static void appendHeader(final String prefix, final String content, final StringBuilder builder) {
        builder.append(prefix);
        builder.append(SPACE);
        builder.append(content);
        builder.append(NEW_LINE);
    }

}
