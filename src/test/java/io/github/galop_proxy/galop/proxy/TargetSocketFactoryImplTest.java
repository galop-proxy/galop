package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.api.network.PortNumber;
import io.github.galop_proxy.galop.configuration.TargetConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link TargetSocketFactoryImpl}.
 */
public class TargetSocketFactoryImplTest {

    @Test
    public void create_createsSocketAndConnectsItToTheSpecifiedPortNumberAtTheSpecifiedIPAddress()
            throws IOException {

        try (final ServerSocket serverSocket = new ServerSocket(0)) {

            final InetAddress serverAddress = InetAddress.getLocalHost();
            final PortNumber serverPort = new PortNumber(serverSocket.getLocalPort());

            final TargetConfiguration configuration = mock(TargetConfiguration.class);
            when(configuration.getAddress()).thenReturn(serverAddress);
            when(configuration.getPort()).thenReturn(serverPort);
            when(configuration.getConnectionTimeout()).thenReturn(0);

            final TargetSocketFactory factory = new TargetSocketFactoryImpl(configuration);

            try (final Socket socket = factory.create()) {
                assertNotNull(socket);
            }

        }

    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new TargetSocketFactoryImpl(null);
    }

}
