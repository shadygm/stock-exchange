package nl.rug.aoop.traderapplication.bot;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestBot {
    private Bot bot;
    private Client mockClient;
    private Strategy mockStrategy;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;

    @BeforeEach
    public void setUp() {
        executorService = Executors.newCachedThreadPool();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        mockClient = Mockito.mock(Client.class);
        mockStrategy = Mockito.mock(Strategy.class);
        bot = new Bot(mockClient, mockStrategy);
    }

    @Test
    public void testConstructor() {
        assertNotNull(bot);
    }

    @Test
    public void testTerminate() {
        bot.terminate();
        verify(mockClient).terminate();
    }

}
