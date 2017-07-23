package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.galop.configuration.HttpHeaderConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.Callable;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class ExchangeHandlerImpl implements ExchangeHandler {

    private static final Logger LOGGER = LogManager.getLogger(ExchangeHandler.class);

    private final HttpHeaderConfiguration httpHeaderConfiguration;
    private final MessageParser messageParser;
    private final MessageWriter messageWriter;
    private final ExecutorService executorService;

    @Inject
    ExchangeHandlerImpl(final HttpHeaderConfiguration httpHeaderConfiguration, final MessageParser messageParser,
                        final MessageWriter messageWriter, final ExecutorService executorService) {
        this.httpHeaderConfiguration = checkNotNull(httpHeaderConfiguration, "httpHeaderConfiguration");
        this.messageParser = checkNotNull(messageParser, "messageParser");
        this.messageWriter = checkNotNull(messageWriter, "messageWriter");
        this.executorService = checkNotNull(executorService, "executorService");
    }

    // Handle request:

    @Override
    public void handleRequest(final Socket source, final Socket target) throws Exception {

        validateParameters(source, target);

        final InputStream inputStream = source.getInputStream();

        try {
            final Request request = parseRequest(inputStream);
            writeRequest(request, inputStream, target.getOutputStream());
        } catch (final Exception ex) {
            handleRequestError(ex, source);
            throw ex;
        }

    }

    private Request parseRequest(final InputStream inputStream) throws Exception {
        final long timeout = httpHeaderConfiguration.getRequest().getReceiveTimeout();
        return executeWithTimeout(() -> messageParser.parseRequest(inputStream), timeout);
    }

    private void writeRequest(final Request request, final InputStream inputStream, final OutputStream outputStream)
            throws Exception {
        messageWriter.writeRequest(request, inputStream, outputStream);
    }

    private void handleRequestError(final Exception ex, final Socket source) {

        if (ex instanceof UnsupportedTransferEncodingException) {
            sendHttpStatusToClient(StatusCode.LENGTH_REQUIRED, source);
        } else if (ex instanceof UnsupportedHttpVersionException) {
            sendHttpStatusToClient(StatusCode.HTTP_VERSION_NOT_SUPPORTED, source);
        } else if (ex instanceof LineTooLargeException) {
            sendHttpStatusToClient(StatusCode.URI_TOO_LONG, source);
        } else if (ex instanceof HeaderFieldsTooLargeException) {
            sendHttpStatusToClient(StatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE, source);
        } else if (ex instanceof InterruptedException) {
            sendHttpStatusToClient(StatusCode.SERVICE_UNAVAILABLE, source);
        } else if (ex instanceof TimeoutException) {
            sendHttpStatusToClient(StatusCode.REQUEST_TIMEOUT, source);
        } else {
            sendHttpStatusToClient(StatusCode.BAD_REQUEST, source);
        }

    }

    // Handle response:

    @Override
    public void handleResponse(final Socket source, final Socket target) throws Exception {

        validateParameters(source, target);

        final InputStream inputStream = target.getInputStream();

        boolean sendingResponseStarted = false;

        try {
            final Response response = parseResponse(inputStream);
            sendingResponseStarted = true;
            writeResponse(response, inputStream, source.getOutputStream());
        } catch (final Exception ex) {
            handleResponseError(ex, source, sendingResponseStarted);
            throw ex;
        }

    }

    private Response parseResponse(final InputStream inputStream) throws Exception {
        final long timeout = httpHeaderConfiguration.getResponse().getReceiveTimeout();
        return executeWithTimeout(() -> messageParser.parseResponse(inputStream), timeout);
    }

    private void writeResponse(final Response response, final InputStream inputStream, final OutputStream outputStream)
            throws Exception {
        messageWriter.writeResponse(response, inputStream, outputStream);
    }

    private void handleResponseError(final Exception ex, final Socket source, final boolean sendingResponseStarted) {

        LOGGER.error("An error occurred while processing the server response.", ex);

        if (sendingResponseStarted) {
            return;
        }

        if (ex instanceof InterruptedException) {
            sendHttpStatusToClient(StatusCode.SERVICE_UNAVAILABLE, source);
        } else if (ex instanceof TimeoutException) {
            sendHttpStatusToClient(StatusCode.GATEWAY_TIMEOUT, source);
        } else {
            sendHttpStatusToClient(StatusCode.BAD_GATEWAY, source);
        }

    }

    // Helper methods:

    private void validateParameters(final Socket source, final Socket target) {
        checkNotNull(source, "source");
        checkNotNull(target, "target");
    }

    private <V> V executeWithTimeout(final Callable<V> task, final long timeout)
            throws IOException, InterruptedException, TimeoutException {

        final Future<V> result = executorService.submit(task);

        try {
            return result.get(timeout, TimeUnit.MILLISECONDS);
        } catch (final ExecutionException ex) {

            final Throwable cause = ex.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw new IOException(cause);
            }

        }

    }

    private void sendHttpStatusToClient(final StatusCode statusCode, final Socket source) {
        try {
            final byte[] response = ResponseBuilder.createWithStatus(statusCode).build();
            IOUtils.write(response, source.getOutputStream());
        } catch (final Exception ex) {
            if (!"socket is closed".equals(getNormalizedMessage(ex))) {
                LOGGER.warn("Could not send HTTP status code " + statusCode + " to the client.", ex);
            }
        }
    }

    private String getNormalizedMessage(final Exception ex) {
        return String.valueOf(ex.getMessage()).toLowerCase(Locale.ENGLISH);
    }

}
