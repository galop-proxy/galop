package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.galop.commons.ByteLimitExceededException;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.http.HttpHeaderParser.Result;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.*;

import static java.util.Objects.requireNonNull;

final class HttpExchangeHandlerImpl implements HttpExchangeHandler {

    private static final Logger LOGGER = LogManager.getLogger(HttpExchangeHandler.class);

    private final HttpHeaderParser httpHeaderParser;
    private final HttpMessageHandler httpMessageHandler;
    private final ExecutorService executorService;

    @Inject
    HttpExchangeHandlerImpl(final HttpHeaderParser httpHeaderParser, final HttpMessageHandler httpMessageHandler,
                            final ExecutorService executorService) {
        this.httpHeaderParser = requireNonNull(httpHeaderParser, "httpHeaderParser must not be null.");
        this.httpMessageHandler = requireNonNull(httpMessageHandler, "httpMessageHandler must not be null.");
        this.executorService = requireNonNull(executorService, "executorService must not be null.");
    }

    // Handle request:

    @Override
    public void handleRequest(final Socket source, final Socket target, final Configuration configuration,
                              final Runnable startHandlingRequestCallback) throws Exception {

        validateParameters(source, target, configuration, startHandlingRequestCallback);

        final InputStream inputStream = new BufferedInputStream(source.getInputStream());

        try {
            final Result header = parseRequestHeader(inputStream, configuration, startHandlingRequestCallback);
            httpMessageHandler.handle(header, inputStream, target.getOutputStream());
        } catch (final Exception ex) {
            handleRequestError(ex, source);
            throw ex;
        }

    }

    private Result parseRequestHeader(final InputStream inputStream, final Configuration configuration,
                                      final Runnable startCallback) throws Exception {
        final long timeout = configuration.getHttpHeaderRequestReceiveTimeout();
        final int maxHeaderSize = configuration.getMaxHttpHeaderSize();
        return executeWithTimeout(() -> httpHeaderParser.parse(inputStream, maxHeaderSize, startCallback), timeout);
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
    public void handleResponse(final Socket source, final Socket target, final Configuration configuration,
                               final Runnable endHandlingResponseCallback) throws Exception {

        validateParameters(source, target, configuration, endHandlingResponseCallback);

        final InputStream inputStream = new BufferedInputStream(target.getInputStream());

        try {
            final Result header = parseResponseHeader(inputStream, configuration);
            httpMessageHandler.handle(header, inputStream, source.getOutputStream());
            endHandlingResponseCallback.run();
        } catch (final Exception ex) {
            handleResponseError(ex, source);
            throw ex;
        }

    }

    private Result parseResponseHeader(final InputStream inputStream, final Configuration configuration)
            throws Exception {
        final long timeout = configuration.getHttpHeaderResponseReceiveTimeout();
        final int maxHeaderSize = configuration.getMaxHttpHeaderSize();
        return executeWithTimeout(() -> httpHeaderParser.parse(inputStream, maxHeaderSize), timeout);
    }

    private void handleResponseError(final Exception ex, final Socket source) {

        LOGGER.error("An error occurred while processing the server response.", ex);

        if (ex instanceof InterruptedException) {
            sendHttpStatusToClient(HttpStatusCode.SERVICE_UNAVAILABLE, source);
        } else if (ex instanceof TimeoutException) {
            sendHttpStatusToClient(HttpStatusCode.GATEWAY_TIMEOUT, source);
        } else {
            sendHttpStatusToClient(HttpStatusCode.BAD_GATEWAY, source);
        }

    }

    // Helper methods:

    private void validateParameters(final Socket source, final Socket target, final Configuration configuration,
                                    final Runnable callback) {
        requireNonNull(source, "source must not be null");
        requireNonNull(target, "target must not be null");
        requireNonNull(configuration, "configuration must not be null");
        requireNonNull(callback, "callback must not be null.");
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
            // Can be ignored.
        }
    }

}
