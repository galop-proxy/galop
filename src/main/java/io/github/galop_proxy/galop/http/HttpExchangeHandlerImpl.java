package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.galop.configuration.HttpHeaderConfiguration;
import io.github.galop_proxy.galop.http.HttpHeaderParser.Result;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class HttpExchangeHandlerImpl implements HttpExchangeHandler {

    private static final Logger LOGGER = LogManager.getLogger(HttpExchangeHandler.class);

    private final HttpHeaderConfiguration httpHeaderConfiguration;
    private final HttpHeaderParser httpHeaderParser;
    private final HttpMessageHandler httpMessageHandler;
    private final ExecutorService executorService;

    @Inject
    HttpExchangeHandlerImpl(final HttpHeaderConfiguration httpHeaderConfiguration, final HttpHeaderParser httpHeaderParser,
                            final HttpMessageHandler httpMessageHandler, final ExecutorService executorService) {
        this.httpHeaderConfiguration = checkNotNull(httpHeaderConfiguration, "httpHeaderConfiguration");
        this.httpHeaderParser = checkNotNull(httpHeaderParser, "httpHeaderParser");
        this.httpMessageHandler = checkNotNull(httpMessageHandler, "httpMessageHandler");
        this.executorService = checkNotNull(executorService, "executorService");
    }

    // Handle request:

    @Override
    public void handleRequest(final Socket source, final Socket target, final Runnable startHandlingRequestCallback)
            throws Exception {

        validateParameters(source, target, startHandlingRequestCallback);

        final InputStream inputStream = new BufferedInputStream(source.getInputStream());

        try {
            final Result header = parseRequestHeader(inputStream, startHandlingRequestCallback);
            httpMessageHandler.handle(header, inputStream, target.getOutputStream());
        } catch (final Exception ex) {
            handleRequestError(ex, source);
            throw ex;
        }

    }

    private Result parseRequestHeader(final InputStream inputStream, final Runnable startCallback) throws Exception {
        final long timeout = httpHeaderConfiguration.getRequest().getReceiveTimeout();
        return executeWithTimeout(() -> httpHeaderParser.parse(inputStream, true, startCallback), timeout);
    }

    private void handleRequestError(final Exception ex, final Socket source) {

        if (ex instanceof UnsupportedTransferEncodingException) {
            sendHttpStatusToClient(HttpStatusCode.LENGTH_REQUIRED, source);
        } else if (ex instanceof ByteLimitExceededException) {
            sendHttpStatusToClient(HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE, source);
        } else if (ex instanceof InterruptedException) {
            sendHttpStatusToClient(HttpStatusCode.SERVICE_UNAVAILABLE, source);
        } else if (ex instanceof TimeoutException) {
            sendHttpStatusToClient(HttpStatusCode.REQUEST_TIMEOUT, source);
        } else {
            sendHttpStatusToClient(HttpStatusCode.BAD_REQUEST, source);
        }

    }

    // Handle response:

    @Override
    public void handleResponse(final Socket source, final Socket target, final Runnable endHandlingResponseCallback)
            throws Exception {

        validateParameters(source, target, endHandlingResponseCallback);

        final InputStream inputStream = new BufferedInputStream(target.getInputStream());

        final AtomicBoolean sendingResponseStarted = new AtomicBoolean(false);

        try {
            final Result header = parseResponseHeader(inputStream, () -> sendingResponseStarted.set(true));
            httpMessageHandler.handle(header, inputStream, source.getOutputStream());
            endHandlingResponseCallback.run();
        } catch (final Exception ex) {
            handleResponseError(ex, source, sendingResponseStarted.get());
            throw ex;
        }

    }

    private Result parseResponseHeader(final InputStream inputStream, final Runnable startCallback) throws Exception {
        final long timeout = httpHeaderConfiguration.getResponse().getReceiveTimeout();
        return executeWithTimeout(() -> httpHeaderParser.parse(inputStream, false, startCallback), timeout);
    }

    private void handleResponseError(final Exception ex, final Socket source, final boolean sendingResponseStarted) {

        LOGGER.error("An error occurred while processing the server response.", ex);

        if (sendingResponseStarted) {
            return;
        }

        if (ex instanceof InterruptedException) {
            sendHttpStatusToClient(HttpStatusCode.SERVICE_UNAVAILABLE, source);
        } else if (ex instanceof TimeoutException) {
            sendHttpStatusToClient(HttpStatusCode.GATEWAY_TIMEOUT, source);
        } else {
            sendHttpStatusToClient(HttpStatusCode.BAD_GATEWAY, source);
        }

    }

    // Helper methods:

    private void validateParameters(final Socket source, final Socket target, final Runnable callback) {
        checkNotNull(source, "source");
        checkNotNull(target, "target");
        checkNotNull(callback, "callback");
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

    private void sendHttpStatusToClient(final HttpStatusCode statusCode, final Socket source) {
        try {
            final byte[] response = HttpResponse.createWithStatus(statusCode).build();
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
