package io.github.galop_proxy.galop.configuration;

final class HttpHeaderResponseConfigurationImpl extends AbstractHttpHeaderTypeConfiguration
        implements HttpHeaderResponseConfiguration {

    private final int statusLineSizeLimit;

    HttpHeaderResponseConfigurationImpl(final long receiveTimeout, final int statusLineSizeLimit, final int fieldsLimit) {
        super(receiveTimeout, fieldsLimit);
        this.statusLineSizeLimit = statusLineSizeLimit;
    }

    @Override
    public int getStatusLineSizeLimit() {
        return statusLineSizeLimit;
    }
}
