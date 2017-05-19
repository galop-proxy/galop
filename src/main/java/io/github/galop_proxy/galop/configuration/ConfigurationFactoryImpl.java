package io.github.galop_proxy.galop.configuration;

import javax.inject.Inject;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class ConfigurationFactoryImpl implements ConfigurationFactory {

    private final ProxyConfigurationFactory proxyConfigurationFactory;
    private final TargetConfigurationFactory targetConfigurationFactory;
    private final HttpConfigurationFactory httpConfigurationFactory;

    @Inject
    ConfigurationFactoryImpl(final ProxyConfigurationFactory proxyConfigurationFactory,
                             final TargetConfigurationFactory targetConfigurationFactory,
                             HttpConfigurationFactory httpConfigurationFactory) {
        this.proxyConfigurationFactory = requireNonNull(proxyConfigurationFactory,
                "proxyConfigurationFactory must not be null.");
        this.targetConfigurationFactory = requireNonNull(targetConfigurationFactory,
                "targetConfigurationFactory must not be null.");
        this.httpConfigurationFactory = requireNonNull(httpConfigurationFactory,
                "httpConfigurationFactory must not be null.");
    }

    @Override
    public Configuration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        requireNonNull(properties, "properties must not be null.");
        final ProxyConfiguration proxy = proxyConfigurationFactory.parse(properties);
        final TargetConfiguration target = targetConfigurationFactory.parse(properties);
        final HttpConfiguration http = httpConfigurationFactory.parse(properties);
        return new ConfigurationImpl(proxy, target, http);
    }

}
