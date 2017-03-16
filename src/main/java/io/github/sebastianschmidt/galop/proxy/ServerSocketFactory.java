package io.github.sebastianschmidt.galop.proxy;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketFactory {

    ServerSocket create(final int port) throws IOException {
        return new ServerSocket(port);
    }

}
