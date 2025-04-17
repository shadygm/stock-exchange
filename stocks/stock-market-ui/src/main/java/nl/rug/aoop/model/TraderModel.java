package nl.rug.aoop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data model of a trader to use as the mdoel.
 */
public class TraderModel implements TraderDataModel {
    private String id;
    private String name;
    private double funds;
    private Map<String, Double> ownedStocks;

    /**
     * Constructor for the trader model.
     * @param id The ID of the trader.
     * @param name The name of the trader.
     * @param funds The funds of the trader.
     * @param ownedStocks The stocks owned by the trader.
     */
    public TraderModel(String id, String name, double funds, Map<String, Double> ownedStocks) {
        this.id = id;
        this.name = name;
        this.funds = funds;
        this.ownedStocks = ownedStocks;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getFunds() {
        return funds;
    }

    @Override
    public List<String> getOwnedStocks() {
        return new ArrayList<>(ownedStocks.keySet());
    }

    @Override
    public Map<String, Double> getStockList() {
        return ownedStocks;
    }

    @Override
    public void setFunds(double funds) {
        this.funds = funds;
    }

    @Override
    public void setStockList(Map<String, Double> stockList) {
        this.ownedStocks = stockList;
    }

    @Override
    public int getNumberOfOwnedShares(String stockSymbol) {
        if(getStockList().containsKey(stockSymbol)) {
            return getStockList().get(stockSymbol).intValue();
        } else {
            return 0;
        }
    }
}
