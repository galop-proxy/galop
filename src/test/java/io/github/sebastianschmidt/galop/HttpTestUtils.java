package io.github.sebastianschmidt.galop;

public class HttpTestUtils {

    private static final String NEW_LINE = "\r\n";

    public static String createGetRequest() {
        return "GET /hello-world.html HTTP/1.1" + NEW_LINE
                + "Host: www.example.com" + NEW_LINE
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
        response.append("HTTP/1.1 200 OK" + NEW_LINE);
        response.append("Server: Test/1.0" + NEW_LINE);
        response.append("Content-Type: text/html" + NEW_LINE);

        if (contentLength != null) {
            response.append("Content-Length: ");
            response.append(contentLength);
            response.append(NEW_LINE);
        }

        if (transferEncoding != null) {
            response.append("Transfer-Encoding: ");
            response.append(transferEncoding);
            response.append(NEW_LINE);
        }

        response.append(NEW_LINE);
        response.append(content);

        return response.toString();

    }

}
