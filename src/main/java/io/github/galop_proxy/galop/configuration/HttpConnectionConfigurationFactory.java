package io.github.galop_proxy.galop.configuration;

import java.util.Map;

interface HttpConnectionConfigurationFactory {

    HttpConnectionConfiguration parse(Map<String, String> properties) throws InvalidConfigurationException;

}
