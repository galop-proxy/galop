package io.github.galop_proxy.galop.configuration;

import java.util.Map;

interface HttpHeaderRequestConfigurationFactory {

    HttpHeaderRequestConfiguration parse(Map<String, String> properties) throws InvalidConfigurationException;

}
