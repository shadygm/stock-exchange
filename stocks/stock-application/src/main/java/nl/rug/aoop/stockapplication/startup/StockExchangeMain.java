package nl.rug.aoop.stockapplication.startup;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.initialization.WebViewFactory;
import nl.rug.aoop.messagequeue.queues.MessageQueue;
import nl.rug.aoop.messagequeue.queues.ThreadSafeQueue;
import nl.rug.aoop.model.StockExchangeModel;
import nl.rug.aoop.networking.server.Server;
import nl.rug.aoop.stockapplication.loaders.LoadStocks;
import nl.rug.aoop.stockapplication.stock.Stocks;
import nl.rug.aoop.traderapplication.loaders.LoadTraders;
import nl.rug.aoop.traderapplication.trader.Traders;

import java.io.IOException;

/**
 * The main class that starts the stock exchange.
 */
@Slf4j
public class StockExchangeMain {
    /**
     * The port to connect to.
     */

    private static final String TRADER_PATH = "/data/traders.yaml";
    private static final String STOCK_PATH = "/data/stocks.yaml";
    private static final String MQ_PORT = "STOCK_EXCHANGE_PORT";
    /**
     * The standard port to connect to if no port is specified.
     */
    private static final int STANDARD_PORT = 6200;

    /**
     * The main method that starts the stock exchange.
     * @param args The arguments.
     */
    public static void main(String[] args) {
        MessageQueue mq = ThreadSafeQueue.getInstance();
        int port = getServerPort();

        Server server = startServer(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Stocks stocks = loadStocks();
        Traders traders = loadTraders();

        StockExchange stockExchange = new StockExchange(server, mq, stocks, traders);
        startStockExchange(stockExchange);
        createView();
    }

    private static int getServerPort() {
        String mqPort = System.getenv(MQ_PORT);
        return (mqPort != null) ? Integer.parseInt(mqPort) : STANDARD_PORT;
    }

    private static Server startServer(int port) {
        try {
            return new Server(port);
        } catch (IOException e) {
            log.error("Server could not start!", e);
            System.exit(0);
            return null;
        }
    }

    private static Stocks loadStocks() {
        LoadStocks loadStocks = new LoadStocks();
        return loadStocks.loadStock(STOCK_PATH);
    }

    private static Traders loadTraders() {
        LoadTraders loadTraders = new LoadTraders();
        return loadTraders.loadTraders(TRADER_PATH);
    }

    private static void startStockExchange(StockExchange stockExchange) {
        Thread stockExchangeThread = new Thread(stockExchange);
        stockExchangeThread.start();
    }

    private static void createView() {
        WebViewFactory svf = new WebViewFactory();
        svf.createView(StockExchangeModel.getINSTANCE());
    }
}
