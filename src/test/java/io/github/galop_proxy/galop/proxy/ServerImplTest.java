package io.github.galop_proxy.galop.proxy;

import io.github.galop_proxy.galop.http.StatusCode;
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

    private ProxySocketFactory proxySocketFactory;
    private TargetSocketFactory targetSocketFactory;
    private ConnectionHandlerFactory connectionHandlerFactory;
    private ExecutorService executorService;
    private Server server;

    private ServerSocket serverSocket;
    private Socket source;
    private Socket target;
    private ConnectionHandler connectionHandler;

    @Before
    public void setUp() throws IOException {

        proxySocketFactory = mock(ProxySocketFactory.class);
        targetSocketFactory = mock(TargetSocketFactory.class);
        connectionHandlerFactory = mock(ConnectionHandlerFactory.class);
        executorService = mock(ExecutorService.class);
        server = new ServerImpl(proxySocketFactory, targetSocketFactory, connectionHandlerFactory, executorService);

        serverSocket = mock(ServerSocket.class);
        when(proxySocketFactory.create()).thenReturn(serverSocket);

        source = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(source);
        final ByteArrayOutputStream sourceOutputStream = new ByteArrayOutputStream();
        when(source.getOutputStream()).thenReturn(sourceOutputStream);

        target = mock(Socket.class);
        when(targetSocketFactory.create()).thenReturn(target);

        connectionHandler = mock(ConnectionHandler.class);
        when(connectionHandlerFactory.create(source, target)).thenReturn(connectionHandler);

    }

    // Handle new connections:

    @Test
    public void run_createsAndExecutesNewConnectionHandlers() {
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);
        server.run();
        verify(connectionHandlerFactory).create(source, target);
        verify(executorService).execute(connectionHandler);
    }

    @Test
    public void run_afterHandlingANewConnections_waitsForNewConnections() {

        final ConnectionHandler secondConnectionHandler = mock(ConnectionHandler.class);
        when(connectionHandlerFactory.create(any(), any())).thenReturn(connectionHandler, secondConnectionHandler);
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);

        server.run();

        verify(connectionHandlerFactory, times(2)).create(source, target);
        verify(executorService).execute(connectionHandler);
        verify(executorService).execute(secondConnectionHandler);

    }

    @Test
    public void run_handleNewConnectionAfterServerSocketWasClosed_closesNewConnection() throws IOException {

        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(connectionHandlerFactory, never()).create(any(), any());
        verify(executorService, never()).execute(any());
        verify(source).close();
        verify(target).close();

    }

    // Error handling:

    @Test(expected = RuntimeException.class)
    public void run_whenConfiguredServerPortIsAlreadyInUse_throwsRuntimeException() throws IOException {
        doThrow(IOException.class).when(proxySocketFactory).create();
        server.run();
    }

    @Test
    public void run_whenCannotConnectToTarget_sendsServerUnavailableMessageToClientAndClosesConnectionSocket()
            throws IOException {

        doThrow(ConnectException.class).when(targetSocketFactory).create();
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();
        assertSourceOutputStreamContains(StatusCode.SERVICE_UNAVAILABLE);

    }

    @Test
    public void run_whenTimeoutWhileConnectingToTarget_sendsGatewayTimeoutMessageToClientAndClosesConnectionSocket()
            throws IOException {

        doThrow(SocketTimeoutException.class).when(targetSocketFactory).create();
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();
        assertSourceOutputStreamContains(StatusCode.GATEWAY_TIMEOUT);

    }

    @Test
    public void run_whenAnErrorOccurredWhileSendingAnErrorMessageToClient_ignoresErrorAndClosesConnectionSocketQuietly()
            throws IOException {

        doThrow(ConnectException.class).when(targetSocketFactory).create();
        doThrow(Exception.class).when(source).getOutputStream();
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();

    }

    @Test
    public void run_whenAnErrorOccurredWhileSendingAnErrorMessageToClient_continuesHandlingNewConnections()
            throws IOException {

        doThrow(ConnectException.class).when(targetSocketFactory).create();
        doThrow(Exception.class).when(source).getOutputStream();
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);

        server.run();

        verify(serverSocket, times(2)).accept();

    }

    @Test
    public void run_whenAnErrorOccurredWhileProcessingANewConnection_closesConnectionSocketsQuietly()
            throws IOException {

        doThrow(Exception.class).when(executorService).execute(any());
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);

        server.run();

        verify(source).close();
        verify(target).close();

    }

    @Test
    public void run_whenAnErrorOccurredWhileProcessingANewConnection_continuesHandlingNewConnections() {

        final ConnectionHandler faultyConnectionHandler = mock(ConnectionHandler.class);
        when(connectionHandlerFactory.create(any(), any()))
                .thenReturn(faultyConnectionHandler).thenReturn(connectionHandler);
        doThrow(Exception.class).when(executorService).execute(faultyConnectionHandler);
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);

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
        when(serverSocket.isClosed()).thenReturn(false).thenReturn(false).thenReturn(true);
        server.run();
        server.close();
        verify(connectionHandler).close();
    }

    // constructor:

    @Test(expected = NullPointerException.class)
    public void constructor_withoutProxySocketFactory_throwsNullPointerException() {
        new ServerImpl(null, targetSocketFactory, connectionHandlerFactory, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutTargetSocketFactory_throwsNullPointerException() {
        new ServerImpl(proxySocketFactory, null, connectionHandlerFactory, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutConnectionHandlerFactory_throwsNullPointerException() {
        new ServerImpl(proxySocketFactory, targetSocketFactory, null, executorService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutExecutorService_throwsNullPointerException() {
        new ServerImpl(proxySocketFactory, targetSocketFactory, connectionHandlerFactory, null);
    }

    // Helper method:

    private void assertSourceOutputStreamContains(final StatusCode statusCode) throws IOException {
        final String output = source.getOutputStream().toString();
        assertTrue(output.contains(statusCode.getCode() + ""));
        assertTrue(output.contains(statusCode.getReason() + ""));
    }

}
