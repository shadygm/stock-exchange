package nl.rug.aoop.traderapplication.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.model.StockDataModel;
import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.networking.networkproducer.NetworkProducer;
import nl.rug.aoop.traderapplication.order.Order;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements a random trading strategy for the bot.
 */
@Slf4j
public class RandomStrategy implements Strategy{
    /**
     * The bound for the random number generator to pick
     * between buying and selling.
     */
    public static final int BOUND = 2;
    private final NetworkProducer networkProducer;
    private final Random random = new Random();
    /**
     * The margin percentage for the random price.
     */
    private static final int MARGIN_PERCENT = 5;
    @Getter
    private final List<Order> sellOrders = new CopyOnWriteArrayList<>();

    @Getter
    private final List<Order> buyOrders = new CopyOnWriteArrayList<>();
    private TraderDataModel trader;
    private double availableCash = 0;

    /**
     * Constructor for the random strategy.
     * @param networkProducer The network producer to send the orders to.
     */
    public RandomStrategy(NetworkProducer networkProducer) {
        this.networkProducer = networkProducer;
    }

    @Override
    public void executeStrategy(List<StockDataModel> stockDataModelList) {
        if(stockDataModelList.isEmpty()) {
            return;
        }
        updateAvailableCash();
        int randomNum = random.nextInt(BOUND);
        switch (randomNum) {
            case 0:
                buyStock(stockDataModelList);
                log.info("Buy order");
                break;
            case 1:
                sellStock(stockDataModelList);
                log.info("Sell order");
                break;
            default:
                log.info("No order");
                break;
        }
    }

    private void updateAvailableCash() {
        availableCash = trader.getFunds();
        for(Order o : buyOrders) {
            availableCash -= o.getPrice() * o.getNumShares();
        }
        if (availableCash > trader.getFunds()) {
            availableCash = trader.getFunds();
        }
    }

    private void sellStock(List<StockDataModel> stockDataModelList) {
        Order order = initSellOrder(stockDataModelList);
        if (order == null) {
            log.info("Order is null");
            return;
        }
        Message message = new Message(Order.getSellOrder(), order.convertToJSON());
        networkProducer.put(message);
    }

    private Order initSellOrder( List<StockDataModel> stockDataModelList) {
        List<String> ownedStocks = trader.getOwnedStocks();
        if (ownedStocks.isEmpty()) {
            return null;
        }
        int counter = 0;
        while (counter < ownedStocks.size()) {
            int randomStock = random.nextInt(ownedStocks.size());
            String stockId = ownedStocks.get(randomStock);
            Double ownedOfStock = trader.getStockList().get(stockId);
            Double stockSold = findStockSoldIfExist(stockId);

            if (ownedOfStock - stockSold > 0) {
                double orderShares = Math.floor(random.nextDouble(ownedOfStock - stockSold));
                if(orderShares == 0) {
                    return null;
                }
                double orderPrice = stockDataModelList.get(randomStock).getPrice();
                double margin = MARGIN_PERCENT / 100.0;
                double minPrice = orderPrice * (1 - margin);
                double maxPrice = orderPrice * (1 + margin);

                double randomPrice = minPrice + random.nextDouble() * (maxPrice - minPrice);
                randomPrice = Math.floor(randomPrice * 100.0) / 100.0;
                return new Order(stockId, trader.getId(), orderShares, randomPrice);
            }
            counter++;
        }
        return null;
    }

    private Double findStockSoldIfExist(String stockId) {
        double stockSold = 0;
        for (Order o : sellOrders) {
            if (o.getStockId().equals(stockId)) {
                stockSold += o.getNumShares();
            }
        }
        return stockSold;
    }

    private void buyStock(List<StockDataModel> stockDataModelList) {
        Order order = initBuyOrder(stockDataModelList);
        if(order == null) {
            log.info("Order is null");
            return;
        }
        log.info("Order is not null");
        Message message = new Message(Order.getBuyOrder(), order.convertToJSON());
        networkProducer.put(message);
    }

    private Order initBuyOrder(List<StockDataModel> stockDataModelList) {

        int randomStock = random.nextInt(stockDataModelList.size());
        StockDataModel stock = stockDataModelList.get(randomStock);

        double margin = MARGIN_PERCENT / 100.0;

        double minPrice = stock.getPrice() * (1 - margin);
        double maxPrice = stock.getPrice() * (1 + margin);

        double randomPrice = minPrice + random.nextDouble() * (maxPrice - minPrice);

        double maxNumShares = Math.floor(availableCash/randomPrice);
        double randomNumShares = Math.floor(random.nextDouble() * maxNumShares);
        if (availableCash < randomPrice || randomNumShares <= 0) {
            log.info("Trader has not enough funds");
            return null;
        }
        log.info("Buyer: " + trader.getId() + " Num shares: " + randomNumShares + " Price: " + randomPrice);
        return new Order(stock.getSymbol(), trader.getId(), randomNumShares, randomPrice);
    }

    @Override
    public void setTrader(TraderDataModel trader) {
        this.trader = trader;
    }

    @Override
    public void updateSell(List<Order> orders) {
        sellOrders.clear();
        sellOrders.addAll(orders);
    }

    @Override
    public void updateBuy(List<Order> orders) {
        buyOrders.clear();
        buyOrders.addAll(orders);
    }
}
