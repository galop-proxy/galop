package io.github.galop_proxy.galop.proxy;

import java.io.IOException;
import java.net.Socket;

interface TargetSocketFactory {

    Socket create() throws IOException;

}
