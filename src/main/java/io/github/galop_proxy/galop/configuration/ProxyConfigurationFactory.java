package io.github.galop_proxy.galop.configuration;

import java.util.Map;

public interface ProxyConfigurationFactory {

    ProxyConfiguration parse(Map<String, String> properties) throws InvalidConfigurationException;

}
