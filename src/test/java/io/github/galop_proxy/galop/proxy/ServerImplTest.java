package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.commons.PortNumber;
import io.github.galop_proxy.galop.commons.ServerSocketFactory;
import io.github.galop_proxy.galop.commons.SocketFactory;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.http.HttpStatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ServerImpl}.
 */
public class ServerImplTest {

    private static final PortNumber SERVER_PORT = new PortNumber(80);
    private static final InetAddress TARGET_ADDRESS = mock(InetAddress.class);
    private static final PortNumber TARGET_PORT = new PortNumber(8080);

    private Configuration configuration;
    private ServerSocketFactory serverSocketFactory;
    private SocketFactory socketFactory;
    private ConnectionHandlerFactory connectionHandlerFactory;
    private ExecutorService executorService;
    private ServerImpl server;

    private ServerSocket serverSocket;
    private Socket source;
    private Socket target;
    private ConnectionHandler connectionHandler;

    @Before
    public void setUp() throws IOException {

        configuration = mock(Configuration.class);
        when(configuration.getProxyPort()).thenReturn(SERVER_PORT);
        when(configuration.getTargetAddress()).thenReturn(TARGET_ADDRESS);
        when(configuration.getTargetPort()).thenReturn(TARGET_PORT);

        serverSocketFactory = mock(ServerSocketFactory.class);
        serverSocket = mock(ServerSocket.class);
        when(serverSocketFactory.create(SERVER_PORT)).thenReturn(serverSocket);

        source = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(source);
        final ByteArrayOutputStream sourceOutputStream = new ByteArrayOutputStream();
        when(source.getOutputStream()).thenReturn(sourceOutputStream);

        socketFactory = mock(SocketFactory.class);
        target = mock(Socket.class);
        when(socketFactory.create(TARGET_ADDRESS, TARGET_PORT, 0)).thenReturn(target);

        connectionHandlerFactory = mock(ConnectionHandlerFactory.class);
        connectionHandler = mock(ConnectionHandler.class);
        when(connectionHandlerFactory.create(configuration, source, target)).thenReturn(connectionHandler);

        executorService = mock(ExecutorService.class);
        server = new ServerImpl(configuration, serverSocketFactory, socketFactory, connectionHandlerFactory,
                executorService);

    }

    // Handle new connections:

    @Test
    public void run_createsAndExecutesNewConnectionHandlers() {
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);
        server.run();
        verify(connectionHandlerFactory).create(configuration, source, target);
        verify(executorService).execute(connectionHandler);
    }

    @Test
    public void run_afterHandlingANewConnections_waitsForNewConnections() {

        final ConnectionHandler secondConnectionHandler = mock(ConnectionHandler.class);
        when(connectionHandlerFactory.create(any(), any(), any())).thenReturn(connectionHandler,
                secondConnectionHandler);
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);

        server.run();

        verify(connectionHandlerFactory, times(2)).create(configuration, source, target);
        verify(executorService).execute(connectionHandler);
        verify(executorService).execute(secondConnectionHandler);

    }

    // Error handling:

    @Test(expected = RuntimeException.class)
    public void run_whenConfiguredServerPortIsAlreadyInUse_throwsRuntimeException() throws IOException {
        doThrow(IOException.class).when(serverSocketFactory).create(SERVER_PORT);
        server.run();
    }

    @Test
    public void run_whenCannotConnectToTarget_sendsServerUnavailableMessageToClientAndClosesConnectionSocket()
            throws IOException {

        doThrow(ConnectException.class).when(socketFactory).create(any(), any(), anyInt());
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();
        assertSourceOutputStreamContains(HttpStatusCode.SERVICE_UNAVAILABLE);

    }

    @Test
    public void run_whenTimeoutWhileConnectingToTarget_sendsGatewayTimeoutMessageToClientAndClosesConnectionSocket()
            throws IOException {

        doThrow(SocketTimeoutException.class).when(socketFactory).create(any(), any(), anyInt());
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();
        assertSourceOutputStreamContains(HttpStatusCode.GATEWAY_TIMEOUT);

    }

    @Test
    public void run_whenAnErrorOccurredWhileSendingAnErrorMessageToClient_ignoresErrorAndClosesConnectionSocketQuietly()
            throws IOException {

        doThrow(ConnectException.class).when(socketFactory).create(any(), any(), anyInt());
        doThrow(Exception.class).when(source).getOutputStream();
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();

    }

    @Test
    public void run_whenAnErrorOccurredWhileSendingAnErrorMessageToClient_continuesHandlingNewConnections()
            throws IOException {

        doThrow(ConnectException.class).when(socketFactory).create(any(), any(), anyInt());
        doThrow(Exception.class).when(source).getOutputStream();
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);

        server.run();

        verify(serverSocket, times(2)).accept();

    }

    @Test
    public void run_whenAnErrorOccurredWhileProcessingANewConnection_closesConnectionSocketsQuietly()
            throws IOException {

        doThrow(Exception.class).when(executorService).execute(any());
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();
        verify(target).close();

    }

    @Test
    public void run_whenAnErrorOccurredWhileProcessingANewConnection_continuesHandlingNewConnections() {

        final ConnectionHandler faultyConnectionHandler = mock(ConnectionHandler.class);
        when(connectionHandlerFactory.create(any(), any(), any()))
                .thenReturn(faultyConnectionHandler).thenReturn(connectionHandler);
        doThrow(Exception.class).when(executorService).execute(faultyConnectionHandler);
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);

        server.run();

        verify(executorService).execute(faultyConnectionHandler);
        verify(executorService).execute(connectionHandler);

    }

    // Close server:

    @Test
    public void close_closesServerSocket() throws IOException {
        when(serverSocket.isClosed()).thenReturn(true);
        server.run();
        server.close();
        verify(serverSocket).close();
    }

    @Test
    public void close_closesAllActiveConnectionHandlers() throws IOException {
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);
        server.run();
        server.close();
        verify(connectionHandler).close();
    }

    // Constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfiguration_throwsNullPointerException() {
        new ServerImpl(null, serverSocketFactory, socketFactory, connectionHandlerFactory, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutServerSocketFactory_throwsNullPointerException() {
        new ServerImpl(configuration, null, socketFactory, connectionHandlerFactory, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutSocketFactory_throwsNullPointerException() {
        new ServerImpl(configuration, serverSocketFactory, null, connectionHandlerFactory, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConnectionHandlerFactory_throwsNullPointerException() {
        new ServerImpl(configuration, serverSocketFactory, socketFactory, null, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new ServerImpl(configuration, serverSocketFactory, socketFactory, connectionHandlerFactory, null);
    }

    // Helper method:

    private void assertSourceOutputStreamContains(final HttpStatusCode httpStatusCode) throws IOException {
        final String output = source.getOutputStream().toString();
        assertTrue(output.contains(httpStatusCode.getCode() + ""));
        assertTrue(output.contains(httpStatusCode.getReason() + ""));
    }

}
