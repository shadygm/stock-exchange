package nl.rug.aoop.traderapplication.bot;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.model.StockDataModel;
import nl.rug.aoop.model.StockExchangeModel;
import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.networking.client.Client;
import nl.rug.aoop.traderapplication.order.Order;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Bot class that reads the stock and trader info from the server and executes the strategy.
 */
@Slf4j
public class Bot implements Runnable {
    /**
     * The header for the stock info message.
     */
    public static final String INFO_STOCK = "Stock Info";
    /**
     * The header for the trader info message.
     */
    public static final String INFO_TRADER = "Trader Info";
    private static final int WAITING_TIME = 4;
    /**
     * The header for the buy update message.
     */
    private static final String BUY_UPDATE = "Update Buy";
    /**
     * The header for the sell update message.
     */
    private static final String SELL_UPDATE = "Update Sell";
    private final Client client;
    private TraderDataModel trader;
    private List<StockDataModel> stockDataModelList;
    private final Strategy strategy;
    private static final Random RANDOM = new Random();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    /**
     * Constructor for the bot.
     * @param client The client to read the info from.
     * @param strategy The strategy to execute.
     */
    public Bot(Client client, Strategy strategy) {
        this.client = client;
        this.strategy = strategy;
    }

    @Override
    public void run() {
        executorService.submit(this::readInfo);
        executorService.submit(this::makeOrder);
    }

    private void makeOrder() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (client.isRunning()) {
                if (trader == null || stockDataModelList == null) {
                    return;
                }
                strategy.setTrader(this.trader);
                log.info("Making order");
                strategy.executeStrategy(stockDataModelList);
            } else {
                scheduledExecutorService.shutdown();
            }
        }, 0, WAITING_TIME, TimeUnit.SECONDS);
    }

    private void readInfo() {
        while (client.isRunning()) {
            String fromStocks = client.readInput();
            log.info("Message received");
            if(fromStocks == null) {
                terminate();
            }
            Message message = Message.convertFromJSON(fromStocks);
            log.info(message.getHeader());

            handleMessage(message);
        }
    }

    private void handleMessage(Message message) {
        switch (message.getHeader()) {
            case INFO_STOCK:
                stockDataModelList = StockExchangeModel.convertStockListFromJSON(message.getBody());
                break;
            case INFO_TRADER:
                trader = StockExchangeModel.convertToTraderFromJSON(message.getBody());
                break;
            case BUY_UPDATE:
                strategy.updateBuy(Order.convertFromJSONList(message.getBody()));
                break;
            case SELL_UPDATE:
                strategy.updateSell(Order.convertFromJSONList(message.getBody()));
                break;
            default:
                log.error("Unknown message header: {}", message.getHeader());
                break;
        }
    }

    /**
     * Properly terminates the bot and its threads.
     */
    public void terminate() {
        client.terminate();
        try {
            scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS);
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Could not join threads.", e);
        }
        if (trader != null) {
            log.info("Bot " + trader.getName() + " stopped.");
        }

    }
}
