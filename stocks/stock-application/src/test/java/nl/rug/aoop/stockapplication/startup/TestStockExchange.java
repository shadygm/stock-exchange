package nl.rug.aoop.stockapplication.startup;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.queues.ThreadSafeQueue;
import nl.rug.aoop.networking.server.Server;
import nl.rug.aoop.stockapplication.stock.Stocks;
import nl.rug.aoop.traderapplication.trader.Traders;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class TestStockExchange {
    private StockExchange stockExchange;;
    private Server server;
    private Thread stockThread;
    private Thread serverThread;
    @BeforeEach
    public void setUp() {

        try {
            server = new Server(0);
        } catch (IOException e) {
            log.error("Could not create server", e);
        }
        serverThread = new Thread(server);
        serverThread.start();
        Awaitility.waitAtMost(10, java.util.concurrent.TimeUnit.SECONDS).until(() -> server.isRunning());
        Stocks stocks = new Stocks();
        Traders traders = new Traders();
        stockExchange = new StockExchange(server, ThreadSafeQueue.getInstance(), stocks, traders);
        stockThread = new Thread(stockExchange);
        stockThread.start();
        Awaitility.waitAtMost(10, java.util.concurrent.TimeUnit.SECONDS).until(() -> stockExchange.isRunning());
    }

    @AfterEach
    public void tearDown() {

        stockExchange.terminate();
        server.terminate();
        try {
            serverThread.join(1000);
            stockThread.join(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Awaitility.waitAtMost(10, java.util.concurrent.TimeUnit.SECONDS).until(() -> !server.isRunning());
    }
    @Test
    public void testConstructor() {
        assertNotNull(stockExchange);
        assertTrue(stockExchange.isRunning());
    }

    @Test
    public void testTerminate() {
        stockExchange.terminate();
        assertFalse(stockExchange.isRunning());
    }
}