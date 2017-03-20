package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.commons.ByteLimitExceededException;
import io.github.sebastianschmidt.galop.configuration.Configuration;
import io.github.sebastianschmidt.galop.http.HttpHeaderParser;
import io.github.sebastianschmidt.galop.http.HttpResponse;
import io.github.sebastianschmidt.galop.http.HttpStatusCode;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

final class ConnectionHandlerImpl implements ConnectionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionHandler.class);

    private final Configuration configuration;
    private final HttpHeaderParser httpHeaderParser;
    private final Socket source;
    private final Socket target;

    private boolean currentlyHandlingRequestOrResponse;
    private boolean connectionShouldBeClosed;

    private InputStream sourceInputStream;
    private InputStream targetInputStream;

    ConnectionHandlerImpl(final Configuration configuration, final HttpHeaderParser httpHeaderParser,
                                 final Socket source, final Socket target) {
        this.configuration = requireNonNull(configuration, "configuration must not be null.");
        this.httpHeaderParser = requireNonNull(httpHeaderParser, "httpHeaderParser must not be null.");
        this.source = requireNonNull(source, "source must not be null.");
        this.target = requireNonNull(target, "target must not be null.");
    }

    @Override
    public void run() {

        try {

            sourceInputStream = new BufferedInputStream(source.getInputStream());
            targetInputStream = new BufferedInputStream(target.getInputStream());

            while (!source.isClosed() && !target.isClosed()
                    && !connectionShouldBeClosed && !Thread.interrupted()) {
                handleNextRequestAndResponse();
            }

        } catch (final IOException ex) {
            // Can be ignored.
        } finally {
            closeConnection();
        }

    }

    private void handleNextRequestAndResponse() throws IOException {
        handleRequest();
        handleResponse();
    }

    private void handleRequest() throws IOException {
        try {
            final long requestLength = httpHeaderParser.calculateTotalLength(sourceInputStream,
                    configuration.getMaxHttpHeaderSize(), this::markStartHandlingRequest);
            IOUtils.copyLarge(sourceInputStream, target.getOutputStream(), 0, requestLength);
        } catch (final ByteLimitExceededException ex) {
            sendHttpStatusToClient(HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE);
            throw ex;
        } catch (final Exception ex) {
            sendHttpStatusToClient(HttpStatusCode.BAD_REQUEST);
            throw ex;
        }
    }
    private void handleResponse() throws IOException {
        try {
            final long responseLength = httpHeaderParser.calculateTotalLength(
                    targetInputStream, configuration.getMaxHttpHeaderSize());
            IOUtils.copyLarge(targetInputStream, source.getOutputStream(), 0, responseLength);
            markEndHandlingResponse();
        } catch (final Exception ex) {
            LOGGER.error("An error occurred while processing the server response.", ex);
            sendHttpStatusToClient(HttpStatusCode.BAD_GATEWAY);
            throw ex;
        }
    }

    private void sendHttpStatusToClient(final HttpStatusCode statusCode) {
        try {
            final byte[] response = HttpResponse.createWithStatus(statusCode).build();
            IOUtils.write(response, source.getOutputStream());
        } catch (final Exception innerEx) {
            // Can be ignored.
        }
    }

    private synchronized void markStartHandlingRequest() {
        currentlyHandlingRequestOrResponse = true;
    }

    private synchronized void markEndHandlingResponse() {
        currentlyHandlingRequestOrResponse = false;
        checkIfConnectionShouldAndCanBeClosed();
    }

    private synchronized void markConnectionShouldBeClosed() {
        connectionShouldBeClosed = true;
        checkIfConnectionShouldAndCanBeClosed();
    }

    private void checkIfConnectionShouldAndCanBeClosed() {
        if (connectionShouldBeClosed && !currentlyHandlingRequestOrResponse) {
            closeConnection();
        }
    }

    private void closeConnection() {
        IOUtils.closeQuietly(source);
        IOUtils.closeQuietly(target);
    }

    @Override
    public void close() throws IOException {
        markConnectionShouldBeClosed();
    }

}
