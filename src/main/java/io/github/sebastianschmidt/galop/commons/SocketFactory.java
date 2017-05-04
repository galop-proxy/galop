package io.github.sebastianschmidt.galop.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public interface SocketFactory {

    Socket create(InetAddress address, PortNumber port, int timeout) throws IOException;

}
