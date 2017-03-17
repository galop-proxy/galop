package io.github.sebastianschmidt.galop.proxy;

import io.github.sebastianschmidt.galop.configuration.Configuration;

import java.net.Socket;

public interface ConnectionHandlerFactory {

    Runnable create(Configuration configuration, Socket source, Socket target);

}
