package io.github.galop_proxy.galop.configuration;

import static io.github.galop_proxy.galop.commons.Preconditions.checkNotNull;

final class HttpHeaderConfigurationImpl implements HttpHeaderConfiguration {

    private final HttpHeaderRequestConfiguration request;
    private final HttpHeaderResponseConfiguration response;

    HttpHeaderConfigurationImpl(final HttpHeaderRequestConfiguration request,
                                final HttpHeaderResponseConfiguration response) {
        this.request = checkNotNull(request, "request");
        this.response = checkNotNull(response, "response");
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
