package nl.rug.aoop.stockapplication.interaction;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.model.StockDataModel;
import nl.rug.aoop.model.StockExchangeModel;
import nl.rug.aoop.model.TraderDataModel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles sending of messages about the stock exchange and themselves
 * to the traders.
 */
@Slf4j
public class PeriodicInfo {

    /**
     * Used as a header for the message to specify the message type.
     */
    public static final String INFO_STOCK = "Stock Info";

    /**
     * Used as a header for the message to specify the message type.
     */
    public static final String INFO_TRADER = "Trader Info";
    private ConnectionManager connectionManager;

    /**
     * Constructor for the connection manager.
     * @param connectionManager The connection manager.
     */
    public PeriodicInfo(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Maps the id of the trader to the message we want to send to them
     * and sends the message.
     * @param stocks The list of stocks.
     * @param traderDataList The list of traders.
     */
    public void sending(List<StockDataModel> stocks, List<TraderDataModel> traderDataList) {
        ConcurrentMap<String, Message> stockMessages = new ConcurrentHashMap<>();
        ConcurrentMap<String, Message> traderMessages = new ConcurrentHashMap<>();

        String stocksMessage = StockExchangeModel.getINSTANCE().convertStockToJSON();
        String traderMessage;

        for (TraderDataModel trader : traderDataList) {
            stockMessages.put(trader.getId(), new Message(INFO_STOCK, stocksMessage));
            traderMessage = StockExchangeModel.getINSTANCE().convertTraderToJSON(trader);
            traderMessages.put(trader.getId(), new Message(INFO_TRADER, traderMessage));
        }

        connectionManager.send(stockMessages);
        connectionManager.send(traderMessages);

        log.info("Periodic info sent");
    }

}
