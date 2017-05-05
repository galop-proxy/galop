package io.github.galop_proxy.galop.administration;

import io.github.galop_proxy.galop.configuration.Configuration;

public interface MonitorFactory {

    Thread create(Configuration configuration);

}
