package io.github.sebastianschmidt.galop.commons;

import java.io.IOException;
import java.net.ServerSocket;

public interface ServerSocketFactory {

    ServerSocket create(int port) throws IOException;

}
