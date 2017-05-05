package io.github.galop_proxy.galop;

import io.github.galop_proxy.galop.administration.MonitorFactory;
import io.github.galop_proxy.galop.administration.ShutdownHandlerFactory;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.configuration.ConfigurationFileLoader;
import io.github.galop_proxy.galop.configuration.InvalidConfigurationException;
import io.github.galop_proxy.galop.proxy.Server;
import io.github.galop_proxy.galop.proxy.ServerFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

/**
 * Tests the class {@link Starter}.
 */
public class StarterTest {

    private Runtime runtime;
    private ConfigurationFileLoader configurationFileLoader;
    private Configuration configuration;
    private MonitorFactory monitorFactory;
    private Thread monitor;
    private ServerFactory serverFactory;
    private Server server;
    private ShutdownHandlerFactory shutdownHandlerFactory;
    private Thread shutdownHandler;
    private Starter starter;

    @Before
    public void setUp() throws Exception {

        runtime = mock(Runtime.class);

        configurationFileLoader = mock(ConfigurationFileLoader.class);
        configuration = mock(Configuration.class);
        when(configurationFileLoader.load(any())).thenReturn(configuration);

        monitorFactory = mock(MonitorFactory.class);
        monitor = mock(Thread.class);
        when(monitorFactory.create(any())).thenReturn(monitor);

        serverFactory = mock(ServerFactory.class);
        server = mock(Server.class);
        when(serverFactory.create(any())).thenReturn(server);

        shutdownHandlerFactory = mock(ShutdownHandlerFactory.class);
        shutdownHandler = mock(Thread.class);
        when(shutdownHandlerFactory.create(any(), any(), any())).thenReturn(shutdownHandler);

        starter = new Starter(runtime, configurationFileLoader, monitorFactory, serverFactory, shutdownHandlerFactory);

    }

    // Valid arguments and configuration file:

    @Test
    public void start_withValidConfigurationFile_startsApplication() throws Exception {

        final String configurationFile = "./valid.properties";
        starter.start(new String[] { configurationFile });

        verify(configurationFileLoader).load(eq(Paths.get(configurationFile)));
        verify(serverFactory).create(configuration);
        verify(monitorFactory).create(configuration);
        verify(shutdownHandlerFactory).create(configuration, server, monitor);

        final InOrder inOrder = inOrder(runtime, monitor, server);
        inOrder.verify(runtime).addShutdownHook(shutdownHandler);
        inOrder.verify(monitor).start();
        inOrder.verify(server).run();

    }

    // Invalid arguments and help:

    @Test
    public void start_withoutPathToConfigurationFile_exitsApplication() {
        starter.start(new String[0]);
        verify(runtime).exit(1);
    }

    @Test
    public void start_withHelpOption_printsHelpAndReturns() {
        starter.start(new String[] { "-help" });
        starter.start(new String[] { "--help" });
    }

    // Invalid configuration file:

    @Test
    public void start_withNotExistingConfigurationFile_exitsApplication() throws Exception {
        doThrow(IOException.class).when(configurationFileLoader).load(any());
        starter.start(new String[] { "./not-existing-file.properties" });
        verify(runtime).exit(1);
    }

    @Test
    public void start_withInvalidConfigurationFile_exitsApplication() throws Exception {
        Mockito.doThrow(InvalidConfigurationException.class).when(configurationFileLoader).load(any());
        starter.start(new String[] { "./invalid.properties" });
        verify(runtime).exit(1);
    }

    // Other errors:

    @Test
    public void start_errorWhileStartingServer_exitsApplication() {
        doThrow(RuntimeException.class).when(serverFactory).create(any());
        starter.start(new String[] { "./valid.properties" });
        verify(runtime).exit(1);
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutRuntime_throwsNullPointerException() {
        new Starter(null, configurationFileLoader, monitorFactory, serverFactory, shutdownHandlerFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfigurationFileLoader_throwsNullPointerException() {
        new Starter(runtime, null, monitorFactory, serverFactory, shutdownHandlerFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutMonitorFactory_throwsNullPointerException() {
        new Starter(runtime, configurationFileLoader, null, serverFactory, shutdownHandlerFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutServerFactory_throwsNullPointerException() {
        new Starter(runtime, configurationFileLoader, monitorFactory, null, shutdownHandlerFactory);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutShutdownHandlerFactory_throwsNullPointerException() {
        new Starter(runtime, configurationFileLoader, monitorFactory, serverFactory, null);
    }
    
}
