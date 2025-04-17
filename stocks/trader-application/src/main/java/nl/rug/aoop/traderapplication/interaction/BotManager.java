package nl.rug.aoop.traderapplication.interaction;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.Client;
import nl.rug.aoop.networking.messagehandlers.MessageHandler;
import nl.rug.aoop.networking.networkproducer.NetworkProducer;
import nl.rug.aoop.traderapplication.bot.Bot;
import nl.rug.aoop.traderapplication.bot.RandomStrategy;
import nl.rug.aoop.traderapplication.bot.Strategy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class that manages the bots.
 */
@Slf4j
public class BotManager {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Getter
    private List<Bot> bots = new CopyOnWriteArrayList<>();
    @Getter
    private List<Client> clients = new CopyOnWriteArrayList<>();
    private InetSocketAddress address;
    private MessageHandler messageHandler;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private AtomicBoolean finishedSetUp = new AtomicBoolean(false);

    /**
     * Constructor for the bot manager.
     * @param address The address of the server.
     * @param messageHandler The message handler to handle the messages.
     * @param numberOfBotsToCreate The number of bots to create.
     */
    public BotManager(InetSocketAddress address, MessageHandler messageHandler, int numberOfBotsToCreate) {
        this.address = address;
        this.messageHandler = messageHandler;
        for(int i = 0; i < numberOfBotsToCreate; i++) {
            buildList();
        }
        finishedSetUp.set(true);
    }

    private void buildList() {
        Client client = null;
        try {
            client = new Client(address, messageHandler);
        } catch (IOException e) {
            log.error("Unable to create client.", e);
            return;
        }

        log.info("Client successfully created.");
        this.clients.add(client);
        executorService.submit(client);
        Client finalClient = client;
        scheduler.schedule(() -> createBot(finalClient), 2,TimeUnit.SECONDS);
    }

    private void createBot(Client client) {
        NetworkProducer np = new NetworkProducer(client);
        Strategy strategy = new RandomStrategy(np);
        Bot bot = new Bot(client, strategy);
        this.bots.add(bot);
        this.executorService.submit(bot);
    }

    /**
     * Getter for the finishedSetUp boolean.
     * @return True if the setup is finished, false otherwise.
     */
    public Boolean getFinishedSetUp() {
        return finishedSetUp.get();
    }
}
