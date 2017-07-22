package io.github.galop_proxy.galop.configuration;

final class HttpHeaderResponseConfigurationImpl extends AbstractHttpHeaderTypeConfiguration
        implements HttpHeaderResponseConfiguration {

    HttpHeaderResponseConfigurationImpl(final long receiveTimeout, final int fieldsLimit, final int maxSize) {
        super(receiveTimeout, fieldsLimit, maxSize);
    }

}
