package io.github.galop_proxy.galop.network;

import io.github.galop_proxy.api.network.InetAddressFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static io.github.galop_proxy.api.commons.Preconditions.checkNotNull;

final class InetAddressFactoryImpl implements InetAddressFactory {

    @Override
    public InetAddress createByName(final String host) throws UnknownHostException {
        checkNotNull(host, "host");
        return InetAddress.getByName(host);
    }

}
