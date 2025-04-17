package nl.rug.aoop.traderapplication.interaction;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.commandhandlers.GenericCommandHandler;
import nl.rug.aoop.networking.messagehandlers.ConvertToMessageHandler;
import nl.rug.aoop.networking.server.Server;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import nl.rug.aoop.networking.messagehandlers.MessageHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
public class TestBotManager {
    private BotManager botManager;
    private static MessageHandler messageHandler;

    private static InetSocketAddress address;
    private static Thread serverThread;
    private static Server server;


    @BeforeAll
    public static void setUpAll() {
        address = new InetSocketAddress("localhost", 12345);
        messageHandler = new ConvertToMessageHandler(new GenericCommandHandler());
        try {
            server = new Server(address.getPort());
            serverThread = new Thread(server);
            serverThread.start();
        } catch (IOException e) {
            log.error("Unable to create server.", e);
        }
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> server.isRunning());
    }

    @BeforeEach
    public void setUp() {
        botManager = new BotManager(address, messageHandler, 2);
        Awaitility.waitAtMost(10, TimeUnit.SECONDS).until(() -> botManager.getFinishedSetUp());
    }

    @AfterAll
    public static void tearDown() {
        try {
            serverThread.join(1000);
        } catch (InterruptedException e) {
            log.info("Interrupted while waiting for server to stop.", e);
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull(botManager);
    }
}
