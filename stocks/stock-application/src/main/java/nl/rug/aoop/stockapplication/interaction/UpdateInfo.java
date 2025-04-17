package nl.rug.aoop.stockapplication.interaction;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.traderapplication.order.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Sends updates to the traders about the current buys and sells they have.
 */
public class UpdateInfo {
    /**
     * Used as a header for the message to specify the message type.
     */
    public static final String UPDATE_SELL = "Update Sell";
    /**
     * Used as a header for the message to specify the message type.
     */
    public static final String UPDATE_BUY = "Update Buy";
    private ConnectionManager connectionManager;

    /**
     * Constructor for the updateInfo.
     * @param connectionManager The connection manager to send the messages.
     */
    public UpdateInfo(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Maps the id of the trader to the message we want to send to them.
     * @param traderId The id of the trader.
     * @param currentSells The list of current sells for a given trader.
     * @param currentBuys The list of current buys for a given trader.
     */
    public void sendUpdate(String traderId, List<Order> currentSells, List<Order> currentBuys) {
        Map<String, Message> sending = new HashMap<>();
        sending.put(traderId, new Message(UPDATE_SELL, Order.convertListToJSON(currentSells)));
        connectionManager.send(sending);
        sending.clear();
        sending.put(traderId, new Message(UPDATE_BUY, Order.convertListToJSON(currentBuys)));
        connectionManager.send(sending);
    }
}
