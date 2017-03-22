package io.github.sebastianschmidt.galop.http;

import static io.github.sebastianschmidt.galop.http.HttpConstants.*;

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
        response.append(HTTP_VERSION + SPACE + "200 OK" + NEW_LINE);
        response.append(HEADER_SERVER_PREFIX + SPACE + "Test/1.0" + NEW_LINE);
        response.append(HEADER_CONTENT_TYPE_PREFIX + SPACE + "text/html" + NEW_LINE);

        if (contentLength != null) {
            response.append(HEADER_CONTENT_LENGTH_PREFIX);
            response.append(SPACE);
            response.append(contentLength);
            response.append(NEW_LINE);
        }

        if (transferEncoding != null) {
            response.append(HEADER_TRANSFER_ENCODING_PREFIX);
            response.append(SPACE);
            response.append(transferEncoding);
            response.append(NEW_LINE);
        }

        response.append(NEW_LINE);
        response.append(content);

        return response.toString();

    }

}
