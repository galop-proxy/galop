package io.github.sebastianschmidt.galop.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public interface SocketFactory {

    Socket create(InetAddress address, int port) throws IOException;

}
