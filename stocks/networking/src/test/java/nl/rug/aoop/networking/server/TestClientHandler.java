package nl.rug.aoop.networking.server;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.Client;
import nl.rug.aoop.networking.messagehandlers.MessageHandler;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the ClientHandler class.
 */
@Slf4j
public class TestClientHandler {

    private ServerSocket serverSocket;
    private ClientHandler clientHandler;
    private Thread handlerThread;
    private MessageHandler mockMessageHandler;
    private Client client;
    private static final int PORT = 0;
    private static final String HOSTNAME = "localhost";

    /**
     * Sets up the server socket and the client handler.
     *
     * @throws IOException if the server socket could not be created.
     */
    @BeforeEach
    public void setUp() throws IOException {
        serverSocket = new ServerSocket(PORT);
        serverSocket.setReuseAddress(true);
        mockMessageHandler = Mockito.mock(MessageHandler.class);

        Thread clientThread = new Thread (() -> {
            try {
                client = new Client(new InetSocketAddress(HOSTNAME, serverSocket.getLocalPort()), mockMessageHandler);
                Awaitility.await().atMost(10, TimeUnit.SECONDS).until(client::isRunning);
            } catch (IOException e) {
                log.error("Could not connect to server", e);
            }
        });

        clientThread.start();
        while (clientHandler == null) {
            try {
                synchronized (this) {
                    clientHandler = new ClientHandler(serverSocket.accept(), mockMessageHandler);
                }

            } catch (IOException e) {
                log.error("Could not accept client", e);
            }
        }

        handlerThread = new Thread(clientHandler);
        handlerThread.start();
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(clientHandler::isRunning);
    }

    /**
     * Closes the server socket and terminates the client handler.
     */
    @AfterEach
    public void teardown() throws InterruptedException {
        clientHandler.terminate();
        handlerThread.join(1000);
    }

    /**
     * Tests the constructor of the class.
     */
    @Test
    public void testConstructor() {
        assertNotNull(clientHandler);
        assertEquals(clientHandler.getClass(), ClientHandler.class);
    }

    /**
     * Tests whether the client handler is running properly.
     */
    @Test
    public void testIsRunning() {
        assertTrue(clientHandler.isRunning());
    }

    /**
     * Tests the termination method and checks for proper termination.
     */
    @Test
    public void testTermination() {
        clientHandler.terminate();
        assertFalse(clientHandler.isRunning());
    }
}