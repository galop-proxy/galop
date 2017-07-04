package io.github.galop_proxy.galop.http;

import io.github.galop_proxy.api.http.Response;
import io.github.galop_proxy.api.http.Version;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;
import static io.github.galop_proxy.galop.http.Constants.STATUS_CODE_RANGE;

final class ResponseImpl extends AbstractMessage implements Response {

    private int statusCode;
    private String reasonPhrase;

    ResponseImpl(final Version version, final int statusCode, final String reasonPhrase) {
        super(version);
        setStatusCode(statusCode);
        setReasonPhrase(reasonPhrase);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void setStatusCode(final int statusCode) {

        if (STATUS_CODE_RANGE.contains(statusCode)) {
            this.statusCode = statusCode;
        } else {
            throw new IllegalArgumentException("The status code is not within the bounds of the range "
                    + STATUS_CODE_RANGE + ".");
        }

    }

    @Override
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public void setReasonPhrase(final String reasonPhrase) {
        this.reasonPhrase = checkNotNull(reasonPhrase, "reasonPhrase");
    }

}
