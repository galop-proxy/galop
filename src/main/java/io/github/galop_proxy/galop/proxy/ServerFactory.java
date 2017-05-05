package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.configuration.Configuration;

public interface ServerFactory {

    Server create(Configuration configuration);

}
