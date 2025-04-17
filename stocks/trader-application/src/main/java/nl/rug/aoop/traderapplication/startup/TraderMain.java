package nl.rug.aoop.traderapplication.startup;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.networking.commandhandlers.GenericCommandHandler;
import nl.rug.aoop.networking.messagehandlers.ConvertToMessageHandler;
import nl.rug.aoop.networking.messagehandlers.MessageHandler;
import nl.rug.aoop.traderapplication.interaction.BotManager;
import nl.rug.aoop.traderapplication.loaders.LoadTraders;
import nl.rug.aoop.traderapplication.managers.TraderManager;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Main class for the trader application.
 */
@Slf4j
public class TraderMain {

    /**
     * The string for the message queue port environment variable.
     */
    private static final String TRADER_PATH = "/data/traders.yaml";
    private static final String MQ_PORT = "STOCK_EXCHANGE_PORT";
    /**
     * The string for the host environment variable.
     */
    private static final String HOST = "STOCK_EXCHANGE_HOST";
    /**
     * The standard port for the message queue.
     */
    private static final int STANDARD_PORT = 6200;
    /**
     * The standard host for the message queue.
     */
    private static final String STANDARD_HOST = "localhost";

    /**
     * Main method for the trader application.
     *
     * @param args The arguments for the main method.
     */
    public static void main(String[] args) {
        int port;
        String host;
        if (System.getenv(MQ_PORT) != null) {
            log.info("Using port from environment variable");
            port = Integer.parseInt(System.getenv(MQ_PORT));
        } else {
            port = STANDARD_PORT;
        }

        if (System.getenv(HOST) != null) {
            log.info("Using host from environment variable");
            host = System.getenv(HOST);
        } else {
            host = STANDARD_HOST;
        }

        log.info("Starting trader application on " + host + ":" + port);
        InetSocketAddress address = new InetSocketAddress(host, port);
        MessageHandler messageHandler = new ConvertToMessageHandler(new GenericCommandHandler());
        LoadTraders loadTraders = new LoadTraders();

        List<TraderDataModel> traderDataModels = TraderManager.initTraderModel(loadTraders.loadTraders(TRADER_PATH));
        int numberOfTraders = traderDataModels.size();
        BotManager botManager = new BotManager(address, messageHandler, numberOfTraders);
    }
}
