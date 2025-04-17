package nl.rug.aoop.stockapplication.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.model.*;
import nl.rug.aoop.traderapplication.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resolves the limit orders.
 */
@Slf4j
public class LimitResolver implements StockResolver {
    @Getter
    private final List<Order> sellOrders = new ArrayList<>();
    @Getter
    private final List<Order> buyOrders = new ArrayList<>();
    private final StockExchangeModel stockExchangeModel = StockExchangeModel.getINSTANCE();

    @Override
    public void resolveBuy(Order order) {
        List<Order> sameStock = getAllOfSameStock(order);
        if (sellOrders.isEmpty() || sameStock.isEmpty()) {
            buyOrders.add(order);
        }

        for (Order o : sameStock) {
            if (order.getPrice() >= o.getPrice() && !order.getTraderId().equals(o.getTraderId())) {
                log.info("\n\n\tBuy match found for " + order.getStockId() + "\n\n");
                if (resolvePurchase(order, o)) {
                    break;
                }
            }
        }
    }

    private boolean resolvePurchase(Order buyOrder, Order sellOrder) {
        Double buyShares = buyOrder.getNumShares();
        Double sellShares = sellOrder.getNumShares();
        double leftOverShares = buyShares - sellShares;
        double sharesSoldActual = buyShares > sellShares ? sellShares : buyShares;
        Double purchasePrice = buyOrder.getPrice();
        StockDataModel stock = stockExchangeModel.getStockByName(buyOrder.getStockId());

        if (!hasEnoughFunds(buyOrder, sharesSoldActual, purchasePrice)) {
            return false;
        }

        if (leftOverShares > 0) {
            resolveMoreBuy(buyOrder, sellOrder, leftOverShares, stock, sellShares, purchasePrice);
        } else if (leftOverShares < 0) {
            handleExcessBuy(buyOrder, sellOrder, stock, buyShares, purchasePrice);
        } else {
            handleEqualShares(buyOrder, sellOrder, stock, buyShares, purchasePrice);
        }

        log.info("Buyer: " + buyOrder.getTraderId() + " Seller: " + sellOrder.getTraderId() + " Stock: "
                + buyOrder.getStockId() + " Price: " + buyOrder.getPrice() + " Buy Shares: " + buyOrder.getNumShares()
                + " Sell Shares: " + sellOrder.getNumShares());
        return true;
    }

    private boolean hasEnoughFunds(Order buyOrder, double sharesSoldActual, Double purchasePrice) {
        TraderDataModel buyer = stockExchangeModel.getTraderById(buyOrder.getTraderId());
        if (buyer.getFunds() <= sharesSoldActual * purchasePrice) {
            log.info("Buyer: " + buyOrder.getTraderId() + " does not have enough funds to buy " + sharesSoldActual
                    + " shares of " + buyOrder.getStockId() + "\n They have " + buyer.getFunds() +
                    " and need " + sharesSoldActual * purchasePrice);
            buyOrders.remove(buyOrder);
            return false;
        }
        return true;
    }

    private void resolveMoreBuy(Order buyOrder, Order sellOrder, double leftOverShares,
                                StockDataModel stock, Double sellShares, Double purchasePrice) {
        Order newOrder = new Order(buyOrder.getStockId(), buyOrder.getTraderId(), leftOverShares, buyOrder.getPrice());
        buyOrders.add(newOrder);
        buyOrders.remove(buyOrder);
        sellOrders.remove(sellOrder);

        updateStockAndTraders(stock, sellShares, purchasePrice, buyOrder, sellOrder);
    }

    private void handleExcessBuy(Order buyOrder, Order sellOrder, StockDataModel stock,
                                 Double buyShares, Double purchasePrice) {
        Order newOrder = new Order(buyOrder.getStockId(), buyOrder.getTraderId(),
                sellOrder.getNumShares(), buyOrder.getPrice());
        addSellsWhilstRemoving(newOrder);
        buyOrders.remove(buyOrder);
        updateStockAndTraders(stock, buyShares, purchasePrice, buyOrder, sellOrder);
    }

    private void handleEqualShares(Order buyOrder, Order sellOrder, StockDataModel stock,
                                   Double buyShares, Double purchasePrice) {
        sellOrders.remove(sellOrder);
        buyOrders.remove(buyOrder);
        updateStockAndTraders(stock, buyShares, purchasePrice, buyOrder, sellOrder);
    }

    private void updateStockAndTraders(StockDataModel stock, Double shares,
                                       Double purchasePrice, Order buyOrder, Order sellOrder) {
        stockExchangeModel.updateStock(new StockModel(stock.getSymbol(), stock.getName(),
                (long) (stock.getSharesOutstanding() - shares), purchasePrice));
        updateBuyer(buyOrder, shares, purchasePrice);
        updateSeller(sellOrder, shares, purchasePrice);
    }

    private void addSellsWhilstRemoving(Order newOrder) {
        List<Order> toRemove = new ArrayList<>();
        for (Order o : sellOrders) {
            if (o.getStockId().equals(newOrder.getStockId()) && o.getTraderId().equals(newOrder.getTraderId())
                    && o.getPrice().equals(newOrder.getPrice()) && o.getNumShares().equals(newOrder.getNumShares())) {
                toRemove.add(o);
            }
        }
        sellOrders.removeAll(toRemove);
        sellOrders.add(newOrder);
    }

    private void updateSeller(Order sellOrder, Double sellShares, Double purchasePrice) {
        TraderDataModel seller = stockExchangeModel.getTraderById(sellOrder.getTraderId());
        double newFunds = seller.getFunds() + (sellShares * purchasePrice);
        Map<String, Double> newStockList = seller.getStockList();
        newStockList.put(sellOrder.getStockId(),
                newStockList.getOrDefault(sellOrder.getStockId(), 0.0) + sellShares);
        stockExchangeModel.updateTrader(newFunds, newStockList, seller.getId());
    }

    private void updateBuyer(Order buyOrder, double sharedSold, Double purchasePrice) {
        TraderDataModel buyer = stockExchangeModel.getTraderById(buyOrder.getTraderId());
        if (buyer == null) {
            log.info("Buyer is null");
            return;
        }
        double newFunds = buyer.getFunds() - (sharedSold * purchasePrice);
        Map<String, Double> newStockList = buyer.getStockList();
        newStockList.put(buyOrder.getStockId(),
                newStockList.getOrDefault(buyOrder.getStockId(), 0.0) + sharedSold);
        stockExchangeModel.updateTrader(newFunds, newStockList, buyer.getId());
    }

    private List<Order> getAllOfSameStock(Order order) {
        List<Order> sameStock = new ArrayList<>();
        for (Order o : sellOrders) {
            if (o.getStockId().equals(order.getStockId())) {
                sameStock.add(o);
            }
        }
        return sameStock;
    }

    @Override
    public void resolveSell(Order order) {
        sellOrders.add(order);
        List<Order> copyBuyOrders = getAllOfSameStock(order);
        if (copyBuyOrders.isEmpty()) {
            return;
        }
        for (Order o : copyBuyOrders) {
            resolveBuy(o);
        }
    }

    @Override
    public List<Order> getSells(String traderId) {
        List<Order> traderSells = new ArrayList<>();
        for (Order o : sellOrders) {
            if (o.getTraderId().equals(traderId)) {
                traderSells.add(o);
            }
        }
        return traderSells;
    }

    @Override
    public List<Order> getBuys(String traderId) {
        List<Order> traderBuys = new ArrayList<>();
        for (Order o : buyOrders) {
            if (o.getTraderId().equals(traderId)) {
                traderBuys.add(o);
            }
        }
        return traderBuys;
    }
}
