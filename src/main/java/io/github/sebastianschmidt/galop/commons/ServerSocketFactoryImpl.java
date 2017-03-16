package io.github.sebastianschmidt.galop.commons;

import java.io.IOException;
import java.net.ServerSocket;

final class ServerSocketFactoryImpl implements ServerSocketFactory {

    @Override
    public ServerSocket create(final int port) throws IOException {
        return new ServerSocket(port);
    }

}
