package io.github.galop_proxy.system_test.server;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

final class ChunkedHandler extends AbstractHandler {

    private final byte[] image;

    ChunkedHandler() throws IOException {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("files/example.png");
        image = IOUtils.toByteArray(inputStream);
        inputStream.close();
    }

    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
                       final HttpServletResponse response) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("transfer-encoding", "lorem, ipsum, chunked");
        baseRequest.setContentType("image/png");

        final OutputStream outputStream = response.getOutputStream();
        outputStream.write(image);
        outputStream.flush();

        baseRequest.setHandled(true);

    }

}
