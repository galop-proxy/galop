package io.github.sebastianschmidt.galop;

public class HttpTestUtils {

    private static final String NEW_LINE = "\r\n";

    public static String createHttpRequest() {
        return "GET /hello-world.html HTTP/1.1" + NEW_LINE
                + "Host: www.example.com" + NEW_LINE
                + NEW_LINE;
    }

    public static String createHttpResponse(final String content) {
        return createHttpResponse(content, content.getBytes().length + "");
    }

    public static String createHttpResponse(final String content, final String contentLength) {
        return "HTTP/1.1 200 OK" + NEW_LINE
                + "Server: Test/1.0" + NEW_LINE
                + "Content-Length: " + contentLength + NEW_LINE
                + "Content-Type: text/html" + NEW_LINE
                + NEW_LINE
                + content;
    }

}
