package nl.rug.aoop.traderapplication.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Class that represents an order.
 */
@Slf4j @Data @Setter
public class Order {
    private static final String BUY_ORDER = "Buy";
    private static final String SELL_ORDER = "Sell";
    private final String stockId;
    private final String traderId;
    private Double numShares;
    private final Double price;
    /**
     * The Gson object to convert the order to JSON.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructor for the order.
     * @param stockId The id of the stock.
     * @param traderId The id of the trader.
     * @param numShares The number of shares.
     * @param price The price of the order.
     */
    public Order(String stockId, String traderId, Double numShares, Double price) {
        this.stockId = stockId;
        this.traderId = traderId;
        this.numShares = numShares;
        this.price = price;
    }

    /**
     * Converts an order to JSON.
     * @return The JSON string.
     */
    public String convertToJSON() {
        return GSON.toJson(this);
    }

    /**
     * Converts a list of orders to JSON.
     * @param orders The list of orders.
     * @return The JSON string.
     */
    public static String convertListToJSON(List<Order> orders) {
        return GSON.toJson(orders);
    }

    /**
     * Converts a JSON string to a list of orders.
     * @param json The JSON string.
     * @return The list of orders.
     */
    public static List<Order> convertFromJSONList(String json) {
        if (json == null)  {
            log.info("JSON is null");
            return null;
        }
        return GSON.fromJson(json, new TypeToken<List<Order>>(){}.getType());
    }

    /**
     * Converts a JSON string to an order.
     * @param json The JSON string.
     * @return The order.
     */
    public static Order convertFromJSON(String json) {
        if (json == null)  {
            log.info("JSON is null");
            return null;
        }
        return GSON.fromJson(json, Order.class);
    }

    /**
     * Getter for the buy order string.
     * @return The buy order string.
     */
    public static String getBuyOrder() {
        return BUY_ORDER;
    }

    /**
     * Getter for the sell order string.
     * @return The sell order string.
     */
    public static String getSellOrder() {
        return SELL_ORDER;
    }

}
