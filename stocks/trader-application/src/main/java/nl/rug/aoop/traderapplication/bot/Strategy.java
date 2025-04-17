package nl.rug.aoop.traderapplication.bot;

import nl.rug.aoop.model.StockDataModel;
import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.traderapplication.order.Order;

import java.util.List;

/**
 * Interface for the strategies used by the bot.
 */
public interface Strategy {

    /**
     * Executes the strategy.
     * @param stockDataModelList The list of stocks to execute the strategy on.
     */
    void executeStrategy(List<StockDataModel> stockDataModelList);

    /**
     * Updates the sell order that are currently in the market.
     * @param orderList The list of sell orders to update.
     */
    void updateSell(List<Order> orderList);

    /**
     * Updates the buy order that are currently in the market.
     * @param orderList The list of buy orders to update.
     */
    void updateBuy(List<Order> orderList);

    /**
     * Sets the trader for the strategy.
     * @param trader The trader to set.
     */
    void setTrader(TraderDataModel trader);
}
