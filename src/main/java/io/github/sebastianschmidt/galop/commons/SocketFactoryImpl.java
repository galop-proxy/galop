package io.github.sebastianschmidt.galop.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

final class SocketFactoryImpl implements SocketFactory {

    @Override
    public Socket create(final InetAddress address, final PortNumber port, final int timeout) throws IOException {
        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress(address, port.getValue()), timeout);
        return socket;
    }

}
