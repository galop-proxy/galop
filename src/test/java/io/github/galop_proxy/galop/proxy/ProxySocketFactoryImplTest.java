package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.network.PortNumber;
import io.github.galop_proxy.galop.configuration.ProxyConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link ProxySocketFactoryImpl}.
 */
public class ProxySocketFactoryImplTest {

    private ProxySocketFactory factory;

    @Before
    public void setUp() {
        final ProxyConfiguration configuration = mock(ProxyConfiguration.class);
        when(configuration.getPort()).thenReturn(new PortNumber(0));
        factory = new ProxySocketFactoryImpl(configuration);
    }

    @Test
    public void create_createsServerSocketAndBindsItToTheSpecifiedLocalPortNumber() throws IOException {
        try (final ServerSocket serverSocket = factory.create()) {
            assertNotNull(serverSocket);
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new ProxySocketFactoryImpl(null);
    }

}
