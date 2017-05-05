package io.github.galop_proxy.galop.commons;

import java.io.IOException;
import java.net.ServerSocket;

public interface ServerSocketFactory {

    ServerSocket create(PortNumber port) throws IOException;

}
