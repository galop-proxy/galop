package io.github.galop_proxy.galop.configuration;

import static java.util.Objects.requireNonNull;

final class HttpHeaderConfigurationImpl implements HttpHeaderConfiguration {

    private final HttpHeaderRequestConfiguration request;
    private final HttpHeaderResponseConfiguration response;

    HttpHeaderConfigurationImpl(final HttpHeaderRequestConfiguration request,
                                final HttpHeaderResponseConfiguration response) {
        this.request = requireNonNull(request, "request must not be null.");
        this.response = requireNonNull(response, "response must not be null.");
    }

    @Override
    public HttpHeaderRequestConfiguration getRequest() {
        return request;
    }

    @Override
    public HttpHeaderResponseConfiguration getResponse() {
        return response;
    }

}
