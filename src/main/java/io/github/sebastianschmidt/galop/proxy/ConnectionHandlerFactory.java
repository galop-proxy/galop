package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;

import java.net.Socket;

interface ConnectionHandlerFactory {

    ConnectionHandler create(Configuration configuration, Socket source, Socket target);

}
