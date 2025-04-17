package nl.rug.aoop.stockapplication.startup;

import lombok.extern.slf4j.Slf4j;

import nl.rug.aoop.messagequeue.consumer.Consumer;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.MessageQueue;
import nl.rug.aoop.model.*;
import nl.rug.aoop.networking.server.Server;
import nl.rug.aoop.stockapplication.interaction.ConnectionManager;
import nl.rug.aoop.stockapplication.interaction.PeriodicInfo;
import nl.rug.aoop.stockapplication.interaction.UpdateInfo;
import nl.rug.aoop.stockapplication.manager.LimitResolver;
import nl.rug.aoop.stockapplication.manager.StockManager;
import nl.rug.aoop.stockapplication.manager.StockResolver;
import nl.rug.aoop.stockapplication.stock.Stocks;
import nl.rug.aoop.traderapplication.managers.TraderManager;
import nl.rug.aoop.traderapplication.order.Order;
import nl.rug.aoop.traderapplication.trader.TraderData;
import nl.rug.aoop.traderapplication.trader.Traders;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The stock exchange class that handles the stock exchange.
 */
@Slf4j
public class StockExchange implements Runnable {
    private Consumer consumer;
    private Server server;
    private AtomicBoolean running = new AtomicBoolean(false);
    private final ConnectionManager connectionManager;
    private final UpdateInfo updateInfo;
    private final StockExchangeDataModel stockExchangeModel = StockExchangeModel.getINSTANCE();
    private final StockResolver resolve;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final ExecutorService threadHandler = Executors.newCachedThreadPool();
    private List<StockDataModel> stockDataModel;
    private List<TraderDataModel> traderDataModel;

    /**
     * Constructor for the stock exchange.
     * @param server The server to connect to.
     * @param mq The message queue to poll from.
     * @param stocks The list of stocks.
     * @param traders The list of traders.
     */
    public StockExchange(Server server, MessageQueue mq, Stocks stocks, Traders traders) {
        this.server = server;
        this.consumer = new Consumer(mq);
        this.connectionManager = new ConnectionManager(listNames(traders), server);
        this.resolve = new LimitResolver();
        this.updateInfo = new UpdateInfo(connectionManager);

        stockDataModel = StockManager.initStockModel(stocks);
        traderDataModel = TraderManager.initTraderModel(traders);
        StockExchangeModel.setStocks(stockDataModel);
        StockExchangeModel.setTraders(traderDataModel);
    }

    private List<String> listNames(Traders traders) {
        List<String> traderNames = new CopyOnWriteArrayList<>();
        for (TraderData td : traders) {
            traderNames.add(td.getId());
        }
        return traderNames;
    }

    @Override
    public void run() {
        threadHandler.submit(connectionManager);
        threadHandler.submit(this::continuousPoll);
        threadHandler.submit(this::sendInfo);
        running.set(true);
    }

    private void sendInfo() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            stockDataModel = StockExchangeModel.getINSTANCE().getStocks();
            traderDataModel = StockExchangeModel.getINSTANCE().getTraders();
            PeriodicInfo periodicInfo = new PeriodicInfo(connectionManager);
            periodicInfo.sending(stockDataModel, traderDataModel);
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void continuousPoll() {
        while (server.isRunning()) {
            Message msg = consumer.poll();
            if (msg == null) {
                continue;
            }
            log.info("Handling order");
            handleMessage(msg);
        }
        log.info("Server has stopped running");
        terminate();
    }

    private void handleMessage(Message msg) {
        Order order = Order.convertFromJSON(msg.getBody());
        if (order == null) {
            log.info("Invalid order received");
            return;
        }

        switch (msg.getHeader()) {
            case "Buy":
                log.info("Buy message received");
                resolve.resolveBuy(order);
                break;
            case "Sell":
                log.info("Sell message received");
                resolve.resolveSell(order);
                break;
        }
        updateInfo.sendUpdate(order.getTraderId(),
                resolve.getSells(order.getTraderId()), resolve.getBuys(order.getTraderId()));
    }

    /**
     * Used to find the state of the stock exchange.
     * @return True if the stock exchange is running, false otherwise.
     */
    public Boolean isRunning() {
        return running.get();
    }

    /**
     * Terminates the stock exchange.
     */
    public void terminate() {
        server.terminate();
        scheduledExecutorService.shutdown();
        threadHandler.shutdown();
        log.info("Stock Exchange has been terminated.");
        running.set(false);
    }
}
