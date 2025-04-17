package nl.rug.aoop.traderapplication.trader;

import lombok.Data;

/**
 * Helper class used to represent a trader for
 * loading from a yaml file.
 */
@Data
public class TraderData {
    private String name;
    private String id;
    private double funds;
    private StockPortfolio stockPortfolio;
}
