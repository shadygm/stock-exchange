package nl.rug.aoop.stockapplication.manager;

import nl.rug.aoop.model.StockDataModel;
import nl.rug.aoop.model.StockModel;
import nl.rug.aoop.stockapplication.stock.StockData;
import nl.rug.aoop.stockapplication.stock.Stocks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Used to initialize the stock model.
 */
public class StockManager {

    /**
     * Initializes the stock model loaded from the yaml.
     *
     * @param stocks The stocks loaded from the yaml.
     * @return The stock model.
     */
    public static List<StockDataModel> initStockModel(Stocks stocks) {
        if(stocks == null || stocks.getStocks() == null || stocks.getStocks().isEmpty()) {
            return new CopyOnWriteArrayList<>();
        }
        List<StockDataModel> stockDataModelList = new CopyOnWriteArrayList<>();
        for (Map.Entry<String, StockData> sd : stocks.getStocks().entrySet()) {
            StockData stockData = sd.getValue();
            stockDataModelList.add(new StockModel(stockData.getSymbol(), stockData.getName(),
                    (long) stockData.getSharesOutstanding(), stockData.getInitialPrice()));
        }
        if (!stockDataModelList.isEmpty()) {
            return stockDataModelList;
        } else {
            return null;
        }
    }
}