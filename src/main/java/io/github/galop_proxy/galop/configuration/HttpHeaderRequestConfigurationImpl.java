package io.github.galop_proxy.galop.configuration;

final class HttpHeaderRequestConfigurationImpl extends AbstractHttpHeaderTypeConfiguration
        implements HttpHeaderRequestConfiguration {

    private final int requestLineSizeLimit;

    HttpHeaderRequestConfigurationImpl(final long receiveTimeout, final int requestLineSizeLimit, final int fieldsLimit) {
        super(receiveTimeout, fieldsLimit);
        this.requestLineSizeLimit = requestLineSizeLimit;
    }

    @Override
    public int getRequestLineSizeLimit() {
        return requestLineSizeLimit;
    }

}
