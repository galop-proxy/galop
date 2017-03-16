package io.github.sebastianschmidt.galop.commons;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.util.Objects.requireNonNull;

final class InetAddressFactoryImpl implements InetAddressFactory {

    @Override
    public InetAddress createByName(final String host) throws UnknownHostException {
        requireNonNull(host, "host must not be null.");
        return InetAddress.getByName(host);
    }

}
