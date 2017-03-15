package io.github.sebastianschmidt.galop;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link ServerSocketFactory}.
 */
public class ServerSocketFactoryTest {

    @Test
    public void create_createsServerSocketAndBindsItToTheSpecifiedLocalPortNumber() throws IOException {

        final ServerSocketFactory factory = new ServerSocketFactory();

        try (final ServerSocket serverSocket = factory.create(0)) {
            assertNotNull(serverSocket);
        }

    }

}
