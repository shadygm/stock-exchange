package nl.rug.aoop.model;

/**
 * Data model of a stock.
 */
public class StockModel implements StockDataModel {
    private final String symbol;
    private final String name;
    private long sharesOutsdanding;
    private double marketCap;
    private double price;

    /**
     * Constructor for the stock model.
     * @param symbol The symbol of the stock.
     * @param name The name of the stock.
     * @param sharesOutstanding The number of shares outstanding.
     * @param price The price of the stock.
     */
    public StockModel(String symbol, String name, long sharesOutstanding, double price) {
        this.symbol = symbol;
        this.name = name;
        this.sharesOutsdanding = sharesOutstanding;
        this.price = price;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSharesOutstanding() {
        return sharesOutsdanding;
    }

    @Override
    public double getMarketCap() {
        return sharesOutsdanding * price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public void setSharesOutstanding(long sharesOutstanding) {
        this.sharesOutsdanding = sharesOutstanding;
    }
}
