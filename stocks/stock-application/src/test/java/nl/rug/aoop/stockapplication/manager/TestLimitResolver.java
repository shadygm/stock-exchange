package nl.rug.aoop.stockapplication.manager;

import nl.rug.aoop.model.*;
import nl.rug.aoop.traderapplication.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestLimitResolver {
    private static final String AAPL = "AAPL";
    private static final String TRADER_ID = "123";
    private static final String STOCK_NAME = "Apple Inc.";
    private static final String TEST_TRADER = "TestTrader";
    private static final String GOOGL = "GOOGL";

    @Mock
    private StockExchangeModel stockExchangeModel;

    private LimitResolver limitResolver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        limitResolver = new LimitResolver();
    }

    @Test
    public void testConstructor() {
        assertNotNull(limitResolver);
    }

    @Test
    public void testResolveBuy() {
        Order buyOrder = new Order(AAPL, TRADER_ID, 10.0, 150.0);
        StockDataModel stock = new StockModel(AAPL, STOCK_NAME, 1000L, 150.0);
        Map<String, Double> ownedStocks = new HashMap<>();
        ownedStocks.put(AAPL, 10.0);
        ownedStocks.put(GOOGL, 20.0);
        TraderDataModel buyer = new TraderModel(TRADER_ID, TEST_TRADER, 1000.0, ownedStocks);

        when(stockExchangeModel.getStockByName(AAPL)).thenReturn(stock);
        when(stockExchangeModel.getTraderById(TRADER_ID)).thenReturn(buyer);

        limitResolver.resolveBuy(buyOrder);

        List<Order> buyOrders = limitResolver.getBuyOrders();
        List<Order> sellOrders = limitResolver.getSellOrders();
        assertEquals(1, buyOrders.size());
        assertEquals(0, sellOrders.size());

        Order placedOrder = buyOrders.get(0);
        assertEquals(AAPL, placedOrder.getStockId());
        assertEquals(TRADER_ID, placedOrder.getTraderId());
        assertEquals(10.0, placedOrder.getNumShares());
        assertEquals(150.0, placedOrder.getPrice());
    }

    @Test
    public void testResolveSell() {
        Order sellOrder = new Order(AAPL, "456", 10.0, 150.0);

        limitResolver.resolveSell(sellOrder);

        List<Order> buyOrders = limitResolver.getBuyOrders();
        List<Order> sellOrders = limitResolver.getSellOrders();
        assertEquals(0, buyOrders.size());
        assertEquals(1, sellOrders.size());

        Order placedOrder = sellOrders.get(0);
        assertEquals(AAPL, placedOrder.getStockId());
        assertEquals("456", placedOrder.getTraderId());
        assertEquals(10.0, placedOrder.getNumShares());
        assertEquals(150.0, placedOrder.getPrice());
    }

    @Test
    public void testGetSells() {
        Order sellOrder1 = new Order(AAPL, TRADER_ID, 10.0, 150.0);
        Order sellOrder2 = new Order("MSFT", TRADER_ID, 20.0, 200.0);

        limitResolver.resolveSell(sellOrder1);
        limitResolver.resolveSell(sellOrder2);

        List<Order> traderSells = limitResolver.getSells(TRADER_ID);
        assertEquals(2, traderSells.size());
    }

    @Test
    public void testGetBuys() {
        Order buyOrder1 = new Order(AAPL, TRADER_ID, 5.0, 160.0);
        Order buyOrder2 = new Order(GOOGL, TRADER_ID, 8.0, 220.0);

        limitResolver.resolveBuy(buyOrder1);
        limitResolver.resolveBuy(buyOrder2);

        List<Order> traderBuys = limitResolver.getBuys(TRADER_ID);
        assertEquals(2, traderBuys.size());
    }
}
