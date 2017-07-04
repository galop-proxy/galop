package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Request;
import io.github.galop_proxy.api.http.Version;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotEmpty;

final class RequestImpl extends AbstractMessage implements Request {

    private String method;
    private String requestTarget;

    RequestImpl(final Version version, final String method, final String requestTarget) {
        super(version);
        setMethod(method);
        setRequestTarget(requestTarget);
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(final String method) {
        this.method = checkNotEmpty(method, "method");
    }

    @Override
    public String getRequestTarget() {
        return requestTarget;
    }

    @Override
    public void setRequestTarget(final String requestTarget) {
        this.requestTarget = checkNotEmpty(requestTarget, "requestTarget");
    }

}
