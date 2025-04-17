package nl.rug.aoop.traderapplication.bot;

import nl.rug.aoop.model.StockDataModel;
import nl.rug.aoop.model.StockModel;
import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.model.TraderModel;
import nl.rug.aoop.networking.networkproducer.NetworkProducer;
import nl.rug.aoop.traderapplication.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestRandomStrategy {
    public static final String AAPL = "AAPL";
    public static final String APPLE = "APPLE";
    public static final long SHARES_APPLE = (long) 150.0;
    public static final double PRICE_APPLE = 10.0;
    public static final long SHARES_GOOGLE = (long) 2000.0;
    public static final String GOOGLE = "Google";
    public static final String GOOG = "GOOG";
    public static final double GOOGLE_PRICE = 5.0;
    public static final String ID = "bot1";
    public static final String TEST_TRADER = "TestTrader";
    public static final int FUNDS = 1234;
    private RandomStrategy randomStrategy;
    private TraderDataModel trader;
    private List<StockDataModel> stockDataModelList;
    private NetworkProducer mockNetworkProducer;
    @BeforeEach
    public void setUp() {
        mockNetworkProducer = Mockito.mock(NetworkProducer.class);
        randomStrategy = new RandomStrategy(mockNetworkProducer);
        Map<String, Double> map = initMap();
        trader = new TraderModel(ID, TEST_TRADER, FUNDS, map);

        stockDataModelList = new ArrayList<>();
        StockDataModel stock1 = new StockModel(AAPL, APPLE, SHARES_APPLE, PRICE_APPLE);
        StockDataModel stock2 = new StockModel(GOOG, GOOGLE, SHARES_GOOGLE, GOOGLE_PRICE);
        stockDataModelList.add(stock1);
        stockDataModelList.add(stock2);
    }

    private Map<String, Double> initMap() {
        Map<String, Double> map = new HashMap<>();
        map.put(AAPL, 10.0);
        map.put(GOOG, 5.0);
        return map;
    }

    @Test
    public void testConstructor() {
        assertNotNull(randomStrategy);
        assertNotNull(randomStrategy.getBuyOrders());
        assertNotNull(randomStrategy.getSellOrders());
    }

    @Test
    public void testExecuteStrategy() {
        randomStrategy.setTrader(trader);
        assertDoesNotThrow(() -> randomStrategy.executeStrategy(stockDataModelList));
    }
    @Test
    public void testUpdateSell() {
        List<Order> sellOrders = new ArrayList<>();
        Order sellOrder1 = new Order(AAPL, TEST_TRADER, 10.0, 155.0);
        Order sellOrder2 = new Order(GOOG, TEST_TRADER, 5.0, 2050.0);
        sellOrders.add(sellOrder1);
        sellOrders.add(sellOrder2);

        randomStrategy.updateSell(sellOrders);

        assertEquals(2, randomStrategy.getSellOrders().size());
        assertEquals(10, randomStrategy.getSellOrders().get(0).getNumShares());
        assertEquals(5, randomStrategy.getSellOrders().get(1).getNumShares());
    }

    @Test
    public void testUpdateBuy() {
        List<Order> buyOrders = new ArrayList<>();
        Order buyOrder1 = new Order(AAPL, TEST_TRADER, 15.0, 148.0);
        Order buyOrder2 = new Order(GOOG, TEST_TRADER, 7.0, 1990.0);
        buyOrders.add(buyOrder1);
        buyOrders.add(buyOrder2);

        randomStrategy.updateBuy(buyOrders);

        assertEquals(2, randomStrategy.getBuyOrders().size());
        assertEquals(15, randomStrategy.getBuyOrders().get(0).getNumShares());
        assertEquals(7, randomStrategy.getBuyOrders().get(1).getNumShares());
    }

}
