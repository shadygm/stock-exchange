package nl.rug.aoop.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Data model of the stock exchange to use as the model.
 */
@Slf4j
public class StockExchangeModel implements StockExchangeDataModel {
    private static List<StockDataModel> stocks = new CopyOnWriteArrayList<>();
    private static List<TraderDataModel> traders = new CopyOnWriteArrayList<>();
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    @Getter
    private static final StockExchangeModel INSTANCE = new StockExchangeModel();

    private StockExchangeModel() {
    }

    @Override
    public StockDataModel getStockByIndex(int index) {
        if (index >= 0 && index < stocks.size()) {
            return stocks.get(index);
        }
        return null;
    }

    @Override
    public int getNumberOfStocks() {
        return stocks.size();
    }

    @Override
    public TraderDataModel getTraderByIndex(int index) {
        if (index >= 0 && index < traders.size()) {
            return traders.get(index);
        }
        return null;
    }

    @Override
    public int getNumberOfTraders() {
        return traders.size();
    }

    /**
     * Converts a JSON string to a list of stocks.
     * @param json The JSON string.
     * @return The list of stocks.
     */
    public static List<StockDataModel> convertStockListFromJSON(String json) {
        List<StockDataModel> newList = GSON.fromJson(json, new TypeToken<List<StockModel>>(){}.getType());
        return newList;
    }

    /**
     * Converts a JSON string to a single traders.
     * @param json The JSON string.
     * @return The trader that the JSON was representing.
     */
    public static TraderDataModel convertToTraderFromJSON(String json) {
        TraderDataModel trader = GSON.fromJson(json, TraderModel.class);
        assert(trader != null);
        log.info("Trader received: " + trader.getName());
        return trader;
    }

    /**
     * Converts the stock list to a JSON string.
     * @return The JSON string.
     */
    public String convertStockToJSON() {
        return GSON.toJson(stocks);
    }

    /**
     * Converts a trader to a JSON string.
     * @param traders The trader to convert.
     * @return The JSON string.
     */
    public String convertTraderToJSON(TraderDataModel traders) {
        return GSON.toJson(traders);
    }

    /**
     * Gets the list of traders.
     * @return The list of traders.
     */
    public List<TraderDataModel> getTraders() {
        return traders;
    }

    /**
     * Gets the list of stocks.
     * @return The list of stocks.
     */
    public List<StockDataModel> getStocks() {
        return stocks;
    }

    /**
     * Gets a stock by its symbol.
     * @param id The symbol of the stock.
     * @return The stock with the given symbol.
     */
    public StockDataModel getStockByName(String id) {
        for(StockDataModel stock : stocks) {
            if(stock.getSymbol().equals(id)) {
                return stock;
            }
        }
        return null;
    }

    /**
     * Updates the stock with the given stock.
     * @param sdm The stock to update.
     */
    public void updateStock(StockDataModel sdm) {
        for(StockDataModel stock : stocks) {
            if(stock.getSymbol().equals(sdm.getSymbol())) {
                stock.setPrice(sdm.getPrice());
                stock.setSharesOutstanding(sdm.getSharesOutstanding());
            }
        }
    }

    /**
     * Updates the trader with the given id.
     * @param funds The new funds of the trader.
     * @param stockList The new stock list of the trader.
     * @param id The id of the trader.
     */
    public void updateTrader(double funds, Map<String, Double> stockList, String id) {
        for(TraderDataModel trader : traders) {
            if(trader.getId().equals(id)) {
                trader.setFunds(funds);
                trader.setStockList(stockList);
                break;
            }
        }
    }

    /**
     * Sets the list of stocks.
     * @param stocks The list of stocks.
     */
    @Synchronized
    public static void setStocks(List<StockDataModel> stocks) {
        if(stocks == null || stocks.isEmpty()) {
            return;
        }
        StockExchangeModel.stocks.clear();
        StockExchangeModel.stocks.addAll(stocks);
    }

    /**
     * Sets the list of traders.
     * @param traders The list of traders.
     */
    @Synchronized
    public static void setTraders(List<TraderDataModel> traders) {
        if(traders == null || traders.isEmpty()) {
            return;
        }
        StockExchangeModel.traders.clear();
        StockExchangeModel.traders.addAll(traders);
    }

    /**
     * Gets a trader by its id.
     * @param id The id of the trader.
     * @return The trader with the given id.
     */
    public TraderDataModel getTraderById(String id) {
        for(TraderDataModel trader : traders) {
            if(trader.getId().equals(id)) {
                return trader;
            }
        }
        return null;
    }
}
