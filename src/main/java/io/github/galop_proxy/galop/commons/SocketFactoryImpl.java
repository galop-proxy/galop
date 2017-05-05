package io.github.galop_proxy.galop.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

final class SocketFactoryImpl implements SocketFactory {

    @Override
    public Socket create(final InetAddress address, final PortNumber port) throws IOException {
        return new Socket(address, port.getValue());
    }

}
