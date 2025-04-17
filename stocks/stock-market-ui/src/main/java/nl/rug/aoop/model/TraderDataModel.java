package nl.rug.aoop.model;

import java.util.List;
import java.util.Map;

/**
 * Data model of a trader.
 * Note that a trader may have more properties; these are just the ones needed for the view.
 */
public interface TraderDataModel {
    /**
     * Retrieves the (unique) ID of the trader.
     *
     * @return The ID of the trader.
     */
    String getId();

    /**
     * Retrieves the name of the trader.
     *
     * @return The name of the trader.
     */
    String getName();

    /**
     * Retrieves the total amount of funds this trader has available for trading.
     *
     * @return The total number of funds.
     */
    double getFunds();

    /**
     * Retrieves a collection of stocks that the trader owns.
     *
     * @return A list of stock symbols that the trader owns.
     */
    List<String> getOwnedStocks();

    /**
     * Retrieves a map of stocks that the trader owns and the number of shares of that stock.
     * @return A map of stock symbols and the number of shares of that stock.
     */
    Map<String, Double> getStockList();

    /**
     * Sets the funds of the trader.
     * @param funds The new funds of the trader.
     */
    void setFunds(double funds);

    /**
     * Sets the stock list of the trader.
     * @param stockList The new stock list of the trader.
     */
    void setStockList(Map<String, Double> stockList);

    /**
     * Retrieves the number of shares a trader has of a given stock.
     *
     * @param stockSymbol The symbol representing the stock.
     * @return The number of shares the trader has of a given stock.
     * If the trader has none, or the symbol does not exist, it returns 0.
     */
    int getNumberOfOwnedShares(String stockSymbol);
}
