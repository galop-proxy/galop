package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Message;
import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;

import java.io.IOException;
import java.io.OutputStreamWriter;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.Constants.HTTP_VERSION_PREFIX;
import static io.github.galop_proxy.galop.http.Constants.NEW_LINE;

final class StartLineWriterImpl implements StartLineWriter {

    @Override
    public void writeRequestLine(final Request request, final OutputStreamWriter outputStreamWriter) throws IOException {

        checkNotNull(request, "request");
        checkNotNull(outputStreamWriter, "outputStreamWriter");

        outputStreamWriter.write(
                  method(request)
                + " "
                + requestTarget(request)
                + " "
                + version(request)
                + NEW_LINE);

        outputStreamWriter.flush();

    }

    @Override
    public void writeStatusLine(final Response response, final OutputStreamWriter outputStreamWriter) throws IOException {

        checkNotNull(response, "response");
        checkNotNull(outputStreamWriter, "outputStreamWriter");

        outputStreamWriter.write(
                  version(response)
                + " "
                + statusCode(response)
                + " "
                + reasonPhrase(response)
                + NEW_LINE);

        outputStreamWriter.flush();

    }

    private String method(final Request request) {
        return request.getMethod();
    }

    private String requestTarget(final Request request) {
        return request.getRequestTarget();
    }

    private String version(final Message message) {
        return HTTP_VERSION_PREFIX + message.getVersion().getMajor() + "." + message.getVersion().getMinor();
    }

    private String statusCode(final Response response) {
        return Integer.toString(response.getStatusCode());
    }

    private String reasonPhrase(final Response response) {
        return response.getReasonPhrase();
    }

}
