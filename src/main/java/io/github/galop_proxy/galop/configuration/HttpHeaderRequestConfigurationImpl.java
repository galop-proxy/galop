package io.github.galop_proxy.galop.configuration;

final class HttpHeaderRequestConfigurationImpl extends AbstractHttpHeaderTypeConfiguration
        implements HttpHeaderRequestConfiguration {

    HttpHeaderRequestConfigurationImpl(final long receiveTimeout, final int maxSize) {
        super(receiveTimeout, maxSize);
    }

}
