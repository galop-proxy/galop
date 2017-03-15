package io.github.sebastianschmidt.galop;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketFactory {

    ServerSocket create(final int port) throws IOException {
        return new ServerSocket(port);
    }

}
