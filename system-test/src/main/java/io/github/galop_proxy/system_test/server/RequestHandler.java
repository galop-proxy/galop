package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

final class RequestHandler extends AbstractHandler {

    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
                       final HttpServletResponse response) throws IOException, ServletException {

        final String result = formatRequestLine(baseRequest) + formatRequestHeader(baseRequest);

        setHeader(response);
        setMessageBody(response, result);
        baseRequest.setHandled(true);

    }

    private String formatRequestLine(final Request request) {
        return request.getMethod() + " "
                + request.getRequestURI() + " "
                + request.getHttpVersion().asString() + "\r\n";
    }

    private String formatRequestHeader(final Request request) {

        final StringBuilder headers = new StringBuilder();

        for (final String name : Collections.list(request.getHeaderNames())) {

            for (final String value : Collections.list(request.getHeaders(name))) {
                headers.append(name).append(": ").append(value).append("\r\n");
            }

        }

        return headers.toString();

    }

    private void setHeader(final HttpServletResponse response) {
        response.setContentType("text/plain; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setMessageBody(final HttpServletResponse response, final String content) throws IOException {
        final PrintWriter out = response.getWriter();
        out.println(content);
    }

}
