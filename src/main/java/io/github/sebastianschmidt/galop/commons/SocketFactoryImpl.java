package io.github.sebastianschmidt.galop.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

final class SocketFactoryImpl implements SocketFactory {

    @Override
    public Socket create(final InetAddress address, final int port) throws IOException {
        return new Socket(address, port);
    }

}
