package io.github.sebastianschmidt.galop.commons;

import io.github.sebastianschmidt.galop.commons.ServerSocketFactoryImpl;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the class {@link ServerSocketFactoryImpl}.
 */
public class ServerSocketFactoryImplTest {

    @Test
    public void create_createsServerSocketAndBindsItToTheSpecifiedLocalPortNumber() throws IOException {

        final ServerSocketFactoryImpl factory = new ServerSocketFactoryImpl();

        try (final ServerSocket serverSocket = factory.create(0)) {
            assertNotNull(serverSocket);
        }

    }

}
