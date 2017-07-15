package io.github.galop_proxy.system_test.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

final class StatusCodeHandler extends AbstractHandler {

    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
                       final HttpServletResponse response) throws IOException, ServletException {

        final String[] parts = target.split("/");
        final String lastPart = parts[parts.length - 1];
        final int statusCode = Integer.parseInt(lastPart);

        response.setStatus(statusCode);
        baseRequest.setHandled(true);

    }

}
