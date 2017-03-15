package io.github.sebastianschmidt.galop;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link SocketFactory}.
 */
public class SocketFactoryTest {

    @Test
    public void create_createsSocketAndConnectsItToTheSpecifiedPortNumberAtTheSpecifiedIPAddress()
            throws IOException {

        final ServerSocketFactory serverSocketFactory = new ServerSocketFactory();
        final SocketFactory socketFactory = new SocketFactory();

        try (final ServerSocket serverSocket = serverSocketFactory.create(0)) {

            final InetAddress serverAddress = InetAddress.getLocalHost();
            final int serverPort = serverSocket.getLocalPort();

            try (final Socket socket = socketFactory.create(serverAddress, serverPort)) {
                assertNotNull(socket);
            }

        }

    }

}
