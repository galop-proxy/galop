package io.github.galop_proxy.galop.configuration;

import javax.inject.Inject;
import java.util.Map;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class ConfigurationFactoryImpl implements ConfigurationFactory {

    private final ProxyConfigurationFactory proxyConfigurationFactory;
    private final TargetConfigurationFactory targetConfigurationFactory;
    private final HttpConfigurationFactory httpConfigurationFactory;

    @Inject
    ConfigurationFactoryImpl(final ProxyConfigurationFactory proxyConfigurationFactory,
                             final TargetConfigurationFactory targetConfigurationFactory,
                             final HttpConfigurationFactory httpConfigurationFactory) {
        this.proxyConfigurationFactory = checkNotNull(proxyConfigurationFactory, "proxyConfigurationFactory");
        this.targetConfigurationFactory = checkNotNull(targetConfigurationFactory, "targetConfigurationFactory");
        this.httpConfigurationFactory = checkNotNull(httpConfigurationFactory, "httpConfigurationFactory");
    }

    @Override
    public Configuration parse(final Map<String, String> properties) throws InvalidConfigurationException {
        checkNotNull(properties, "properties");
        final ProxyConfiguration proxy = proxyConfigurationFactory.parse(properties);
        final TargetConfiguration target = targetConfigurationFactory.parse(properties);
        final HttpConfiguration http = httpConfigurationFactory.parse(properties);
        return new ConfigurationImpl(proxy, target, http);
    }

}
