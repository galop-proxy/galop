package io.github.sebastianschmidt.galop;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketFactory {

    public Socket create(final InetAddress address, final int port) throws IOException {
        return new Socket(address, port);
    }

}
