package nl.rug.aoop.stockapplication.interaction;

import nl.rug.aoop.traderapplication.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TestUpdateInfo {
    private ConnectionManager mockConnectionManager;
    private static final String TRADER_ID = "testTrader";

    private UpdateInfo updateInfo;

    @BeforeEach
    public void setUp() {
        mockConnectionManager = Mockito.mock(ConnectionManager.class);
        updateInfo = new UpdateInfo(mockConnectionManager);
    }

    @Test
    public void testConstructor() {
        assertNotNull(updateInfo);
    }

    @Test
    public void testSendUpdate() {
        List<Order> currentSells = new ArrayList<>();
        currentSells.add(new Order("AAPL", TRADER_ID, 10.0, 150.0));
        List<Order> currentBuys = new ArrayList<>();
        currentBuys.add(new Order("AAPL", TRADER_ID, 10.0, 100.0));

        updateInfo.sendUpdate(TRADER_ID, currentSells, currentBuys);

        verify(mockConnectionManager, times(2)).send(anyMap());
    }
}
