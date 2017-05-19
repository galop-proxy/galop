package io.github.galop_proxy.galop.configuration;

import java.util.Map;

interface HttpConfigurationFactory {

    HttpConfiguration parse(Map<String, String> properties) throws InvalidConfigurationException;

}
