package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.parser.HttpHeaderParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

final class ConnectionHandler implements Runnable {

    private final Configuration configuration;
    private final HttpHeaderParser httpHeaderParser;
    private final Socket source;
    private final Socket target;

    public ConnectionHandler(final Configuration configuration, final HttpHeaderParser httpHeaderParser,
                             final Socket source, final Socket target) {
        this.configuration = requireNonNull(configuration, "configuration must not be null.");
        this.httpHeaderParser = requireNonNull(httpHeaderParser, "httpHeaderParser must not be null.");
        this.source = requireNonNull(source, "source must not be null.");
        this.target = requireNonNull(target, "target must not be null.");
    }

    @Override
    public void run() {

        while (!source.isClosed() && !target.isClosed() && !Thread.interrupted()) {

            try {
                handleNextRequestAndResponse();
            } catch (final IOException ex) {
                break;
            }

        }

        IOUtils.closeQuietly(source);
        IOUtils.closeQuietly(target);

    }

    private void handleNextRequestAndResponse() throws IOException {
        handleRequest();
        handleResponse();
    }

    private void handleRequest() throws IOException {
        final long requestLength = httpHeaderParser.calculateTotalLength(
                source.getInputStream(), configuration.getMaxHttpHeaderSize());
        IOUtils.copyLarge(source.getInputStream(), target.getOutputStream(), 0, requestLength);
    }

    private void handleResponse() throws IOException {
        final long responseLength = httpHeaderParser.calculateTotalLength(
                target.getInputStream(), configuration.getMaxHttpHeaderSize());
        IOUtils.copyLarge(target.getInputStream(), source.getOutputStream(), 0, responseLength);
    }

}
