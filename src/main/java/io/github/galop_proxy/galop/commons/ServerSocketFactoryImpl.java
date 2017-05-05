package io.github.galop_proxy.galop.commons;

import java.io.IOException;
import java.net.ServerSocket;

final class ServerSocketFactoryImpl implements ServerSocketFactory {

    @Override
    public ServerSocket create(final PortNumber port) throws IOException {
        return new ServerSocket(port.getValue());
    }

}
