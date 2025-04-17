package nl.rug.aoop.traderapplication.trader;

import lombok.Data;

import java.util.Map;

/**
 * Class that represents the stock portfolio of a trader.
 * Used to load the traders from a yaml file.
 */
@Data
public class StockPortfolio {
    private Map<String, Double> ownedShares;
}
