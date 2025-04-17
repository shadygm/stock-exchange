package nl.rug.aoop.stockapplication.stock;

import lombok.Data;

import java.util.*;

/**
 * Used as a class to load the stocks from the yaml.
 */
@Data
public class Stocks {
    private Map<String, StockData> stocks;
}
