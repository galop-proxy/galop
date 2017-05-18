package io.github.galop_proxy.galop.configuration;

import java.util.Map;

interface HttpHeaderConfigurationFactory {

    HttpHeaderConfiguration parse(Map<String, String> properties) throws InvalidConfigurationException;

}
