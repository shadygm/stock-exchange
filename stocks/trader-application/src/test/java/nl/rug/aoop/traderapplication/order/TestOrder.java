package nl.rug.aoop.traderapplication.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestOrder {

    public static final String STOCK_ID = "AAPL";
    public static final String TRADER_ID = "12345";
    public static final double NUM_SHARES = 10.0;
    public static final double PRICE = 150.0;
    private Order order;

    @BeforeEach
    public void setUp() {
        order = new Order(STOCK_ID, TRADER_ID, NUM_SHARES, PRICE);
    }

    @Test
    public void testConstructor() {
        assertNotNull(order);
        assertEquals(STOCK_ID, order.getStockId());
        assertEquals(TRADER_ID, order.getTraderId());
        assertEquals(NUM_SHARES, order.getNumShares());
        assertEquals(PRICE, order.getPrice());
    }
    @Test
    public void testConvertToJSON() {
        String json = order.convertToJSON();
        assertNotNull(json);
    }

    @Test
    public void testConvertFromJSON() {
        String json = order.convertToJSON();
        Order parsedOrder = Order.convertFromJSON(json);
        assertNotNull(parsedOrder);

        assertEquals(order.getStockId(), parsedOrder.getStockId());
        assertEquals(order.getTraderId(), parsedOrder.getTraderId());
        assertEquals(order.getNumShares(), parsedOrder.getNumShares());
        assertEquals(order.getPrice(), parsedOrder.getPrice(), 0.01);
    }

    @Test
    public void testConvertListToJSON() {
        List<Order> orders = List.of(
                new Order("AAPL", "12345", 10.0, 150.0),
                new Order("GOOG", "54321", 5.0, 3000.0)
        );
        String json = Order.convertListToJSON(orders);
        assertNotNull(json);
    }

    @Test
    public void testConvertFromJSONList() {
        List<Order> orders = List.of(
                new Order("AAPL", "12345", 10.0, 150.0),
                new Order("GOOG", "54321", 5.0, 3000.0)
        );
        String json = Order.convertListToJSON(orders);
        List<Order> parsedOrders = Order.convertFromJSONList(json);
        assertNotNull(parsedOrders);
        assertEquals(orders.size(), parsedOrders.size());
        assertEquals(orders.get(0).getStockId(), parsedOrders.get(0).getStockId());
        assertEquals(orders.get(0).getTraderId(), parsedOrders.get(0).getTraderId());
        assertEquals(orders.get(0).getNumShares(), parsedOrders.get(0).getNumShares());
        assertEquals(orders.get(0).getPrice(), parsedOrders.get(0).getPrice(), 0.01);
    }

    @Test
    public void testConvertFromJSONWithNull() {
        String json = null;
        Order parsedOrder = Order.convertFromJSON(json);
        assertNull(parsedOrder);
    }

    @Test
    public void testConvertListFromJSONWithNull() {
        List<Order> parsedOrders = Order.convertFromJSONList(null);
        assertNull(parsedOrders);
    }
}
