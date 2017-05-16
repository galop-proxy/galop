package io.github.galop_proxy.galop.commons;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link SocketFactoryImpl}.
 */
public class SocketFactoryImplTest {

    @Test
    public void create_createsSocketAndConnectsItToTheSpecifiedPortNumberAtTheSpecifiedIPAddress()
            throws IOException {

        final ServerSocketFactoryImpl serverSocketFactory = new ServerSocketFactoryImpl();
        final SocketFactoryImpl socketFactory = new SocketFactoryImpl();

        try (final ServerSocket serverSocket = serverSocketFactory.create(new PortNumber(0))) {

            final InetAddress serverAddress = InetAddress.getLocalHost();
            final PortNumber serverPort = new PortNumber(serverSocket.getLocalPort());

            try (final Socket socket = socketFactory.create(serverAddress, serverPort, 0)) {
                assertNotNull(socket);
            }

        }

    }

}
