package io.github.galop_proxy.galop.configuration;

import java.util.Map;

interface TargetConfigurationFactory {

    TargetConfiguration parse(Map<String, String> properties) throws InvalidConfigurationException;

}
