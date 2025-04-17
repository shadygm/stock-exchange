package nl.rug.aoop.stockapplication.stock;

import lombok.Data;

/**
 * Used as a class to load the stocks from the yaml.
 */
@Data
public class StockData {
    private String symbol;
    private String name;
    private double sharesOutstanding;
    private double initialPrice;
}
