package nl.rug.aoop.stockapplication.manager;

import nl.rug.aoop.traderapplication.order.Order;

import java.util.List;

/**
 * Interface for the stock resolvers used to resolve the orders.
 */
public interface StockResolver {

    /**
     * Resolves the buy order.
     *
     * @param order The order to be resolved.
     */
    void resolveBuy(Order order);

    /**
     * Resolves the sell order.
     *
     * @param order The order to be resolved.
     */
    void resolveSell(Order order);

    /**
     * Gets the list of buys for a given trader.
     *
     * @param traderId The id of the trader.
     * @return The list of buys for the given trader.
     */
    List<Order> getBuys(String traderId);

    /**
     * Gets the list of sells for a given trader.
     *
     * @param traderId The id of the trader.
     * @return The list of sells for the given trader.
     */
    List<Order> getSells(String traderId);
}
