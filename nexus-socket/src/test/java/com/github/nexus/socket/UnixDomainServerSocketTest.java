//package com.github.nexus.socket;
//
//import com.github.nexus.junixsocket.adapter.UnixSocketFactory;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.catchThrowable;
//import static org.mockito.Mockito.*;
//
//public class UnixDomainServerSocketTest {
//
//    private Path socketFile = Paths.get(System.getProperty("java.io.tempdir"), "junit.txt");
//
//    private UnixSocketFactory mockUnixSocketFactory;
//
//    private UnixDomainServerSocket unixDomainServerSocket;
//
//    @Before
//    public void setUp() {
//        mockUnixSocketFactory = mock(UnixSocketFactory.class);
//        unixDomainServerSocket = new UnixDomainServerSocket(mockUnixSocketFactory);
//    }
//
//    @After
//    public void tearDown() throws IOException {
//        verifyNoMoreInteractions(mockUnixSocketFactory);
//        Files.deleteIfExists(socketFile);
//    }
//
//    @Test
//    public void testServerCreate() throws IOException {
//        final String path = "/tmp";
//        final String filename = "tst1.ipc";
//        final Path socketPath = Paths.get(path, filename);
//
//        unixDomainServerSocket.create(socketPath);
//
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//
//    }
//
//    @Test
//    public void testServerCreateThrowsIOException() throws Exception {
//
//        final String path = "/tmp";
//        final String filename = "tst1.ipc";
//
//        IOException exception = new IOException("BANG!!");
//        final Path socketPath = Paths.get(path, filename);
//
//        doThrow(exception).when(mockUnixSocketFactory).createServerSocket(any(Path.class));
//
//        final Throwable ex = catchThrowable(() -> unixDomainServerSocket.create(socketPath));
//        assertThat(ex)
//            .isInstanceOf(NexusSocketException.class)
//            .hasMessageContaining("BANG!!")
//            .hasCauseExactlyInstanceOf(IOException.class);
//
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//    }
//
//    @Test
//    public void connect() throws IOException {
//        ServerSocket serverSocket = mock(ServerSocket.class);
//
//        when(mockUnixSocketFactory.createServerSocket(any(Path.class))).thenReturn(serverSocket);
//
//        unixDomainServerSocket.create(socketFile);
//
//        unixDomainServerSocket.connect();
//
//        verify(serverSocket).accept();
//        verifyNoMoreInteractions(serverSocket);
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//    }
//
//    @Test
//    public void connectThrowsIOException() throws IOException {
//        ServerSocket serverSocket = mock(ServerSocket.class);
//
//        when(mockUnixSocketFactory.createServerSocket(any(Path.class))).thenReturn(serverSocket);
//
//        unixDomainServerSocket.create(socketFile);
//
//        doThrow(IOException.class).when(serverSocket).accept();
//
//        final Throwable ex = catchThrowable(unixDomainServerSocket::connect);
//        assertThat(ex).isInstanceOf(NexusSocketException.class).hasCauseExactlyInstanceOf(IOException.class);
//
//        verify(serverSocket).accept();
//        verifyNoMoreInteractions(serverSocket);
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//    }
//
//    @Test
//    public void connectAndRead() throws IOException {
//
//        final String data = "HELLOW-99";
//
//        Socket mockSocket = mock(Socket.class);
//
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
//
//        when(mockSocket.getInputStream()).thenReturn(inputStream);
//
//        ServerSocket serverSocket = mock(ServerSocket.class);
//        when(serverSocket.accept()).thenReturn(mockSocket);
//
//        when(mockUnixSocketFactory.createServerSocket(any(Path.class))).thenReturn(serverSocket);
//
//        unixDomainServerSocket.create(socketFile);
//
//        unixDomainServerSocket.connect();
//
//        final byte[] result = unixDomainServerSocket.read();
//
//        //TODO: Verify that the right padding is correct behaviour
//        assertThat(new String(result)).startsWith(data);
//
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//
//        verify(serverSocket).accept();
//        verify(mockSocket).getInputStream();
//    }
//
//    @Test
//    public void connectAndWrite() throws IOException {
//
//        final String data = "HELLOW-99";
//
//        Socket mockSocket = mock(Socket.class);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        when(mockSocket.getOutputStream()).thenReturn(outputStream);
//
//        ServerSocket serverSocket = mock(ServerSocket.class);
//        when(serverSocket.accept()).thenReturn(mockSocket);
//
//        when(mockUnixSocketFactory.createServerSocket(any(Path.class))).thenReturn(serverSocket);
//
//        unixDomainServerSocket.create(socketFile);
//
//        unixDomainServerSocket.connect();
//
//        unixDomainServerSocket.write(data.getBytes());
//
//        assertThat(data).isEqualTo(new String(outputStream.toByteArray()));
//
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//
//        verify(serverSocket).accept();
//        verify(mockSocket).getOutputStream();
//        verifyNoMoreInteractions(serverSocket, mockSocket);
//    }
//
//    @Test
//    public void connectAndReadThrowsException() throws IOException {
//
//        Socket mockSocket = mock(Socket.class);
//
//        doThrow(IOException.class).when(mockSocket).getInputStream();
//
//        ServerSocket serverSocket = mock(ServerSocket.class);
//        when(serverSocket.accept()).thenReturn(mockSocket);
//
//        when(mockUnixSocketFactory.createServerSocket(any(Path.class))).thenReturn(serverSocket);
//
//        unixDomainServerSocket.create(socketFile);
//        unixDomainServerSocket.connect();
//
//        final Throwable ex = catchThrowable(unixDomainServerSocket::read);
//        assertThat(ex).isInstanceOf(NexusSocketException.class).hasCauseExactlyInstanceOf(IOException.class);
//
//
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//
//        verify(serverSocket).accept();
//        verify(mockSocket).getInputStream();
//        verifyNoMoreInteractions(serverSocket, mockSocket);
//    }
//
//    @Test
//    public void connectAndWriteThrowsException() throws IOException {
//
//        Socket mockSocket = mock(Socket.class);
//
//        doThrow(IOException.class).when(mockSocket).getOutputStream();
//
//        ServerSocket serverSocket = mock(ServerSocket.class);
//        when(serverSocket.accept()).thenReturn(mockSocket);
//
//        when(mockUnixSocketFactory.createServerSocket(any(Path.class))).thenReturn(serverSocket);
//
//        unixDomainServerSocket.create(socketFile);
//        unixDomainServerSocket.connect();
//
//        final Throwable ex = catchThrowable(() -> unixDomainServerSocket.write("HELLOW".getBytes()));
//        assertThat(ex).isInstanceOf(NexusSocketException.class).hasCauseExactlyInstanceOf(IOException.class);
//
//
//        verify(mockUnixSocketFactory).createServerSocket(any(Path.class));
//        verify(serverSocket).accept();
//        verify(mockSocket).getOutputStream();
//        verifyNoMoreInteractions(serverSocket, mockSocket);
//    }
//}

package com.github.nexus.socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

public class UnixDomainServerSocketTest {

    private Path socketFile = Paths.get(System.getProperty("java.io.tempdir"), "junit.txt");

    private ServerSocket serverSocket;

    private UnixDomainServerSocket unixDomainServerSocket;

    @Before
    public void setUp() {
        serverSocket = mock(ServerSocket.class);
        unixDomainServerSocket = new UnixDomainServerSocket(serverSocket);
    }

    @After
    public void tearDown() throws IOException {
        verifyNoMoreInteractions(serverSocket);
        Files.deleteIfExists(socketFile);
    }

    @Test
    public void connect() throws IOException {
        unixDomainServerSocket.connect();

        verify(serverSocket).accept();
        verifyNoMoreInteractions(serverSocket);
    }

    @Test
    public void connectThrowsIOException() throws IOException {
        doThrow(IOException.class).when(serverSocket).accept();

        final Throwable ex = catchThrowable(unixDomainServerSocket::connect);
        assertThat(ex).isInstanceOf(NexusSocketException.class).hasCauseExactlyInstanceOf(IOException.class);

        verify(serverSocket).accept();
        verifyNoMoreInteractions(serverSocket);
    }

    @Test
    public void connectAndRead() throws IOException {

        final String data = "HELLOW-99";

        final Socket mockSocket = mock(Socket.class);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());

        doReturn(inputStream).when(mockSocket).getInputStream();

        doReturn(mockSocket).when(serverSocket).accept();

        unixDomainServerSocket.connect();

        final byte[] result = unixDomainServerSocket.read();

        //TODO: Verify that the right padding is correct behaviour
        assertThat(new String(result)).startsWith(data);

        verify(serverSocket).accept();
        verify(mockSocket).getInputStream();
    }

    @Test
    public void connectAndWrite() throws IOException {

        final String data = "HELLOW-99";

        final Socket mockSocket = mock(Socket.class);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        doReturn(outputStream).when(mockSocket).getOutputStream();

        doReturn(mockSocket).when(serverSocket).accept();

        unixDomainServerSocket.connect();

        unixDomainServerSocket.write(data.getBytes());

        assertThat(data).isEqualTo(new String(outputStream.toByteArray()));

        verify(serverSocket).accept();
        verify(mockSocket).getOutputStream();
        verifyNoMoreInteractions(serverSocket, mockSocket);
    }

    @Test
    public void connectAndReadThrowsException() throws IOException {

        Socket mockSocket = mock(Socket.class);

        doThrow(IOException.class).when(mockSocket).getInputStream();

        when(serverSocket.accept()).thenReturn(mockSocket);

        unixDomainServerSocket.connect();

        final Throwable ex = catchThrowable(unixDomainServerSocket::read);
        assertThat(ex).isInstanceOf(NexusSocketException.class).hasCauseExactlyInstanceOf(IOException.class);

        verify(serverSocket).accept();
        verify(mockSocket).getInputStream();
        verifyNoMoreInteractions(serverSocket, mockSocket);
    }

    @Test
    public void connectAndWriteThrowsException() throws IOException {

        Socket mockSocket = mock(Socket.class);

        doThrow(IOException.class).when(mockSocket).getOutputStream();

        when(serverSocket.accept()).thenReturn(mockSocket);

        unixDomainServerSocket.connect();

        final Throwable ex = catchThrowable(() -> unixDomainServerSocket.write("HELLOW".getBytes()));
        assertThat(ex).isInstanceOf(NexusSocketException.class).hasCauseExactlyInstanceOf(IOException.class);


        verify(serverSocket).accept();
        verify(mockSocket).getOutputStream();
        verifyNoMoreInteractions(serverSocket, mockSocket);
    }
}