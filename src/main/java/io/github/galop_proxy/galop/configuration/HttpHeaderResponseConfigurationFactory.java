package io.github.galop_proxy.galop.configuration;

import java.util.Map;

interface HttpHeaderResponseConfigurationFactory {

    HttpHeaderResponseConfiguration parse(Map<String, String> properties) throws InvalidConfigurationException;

}
