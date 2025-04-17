package nl.rug.aoop.stockapplication.manager;

import nl.rug.aoop.model.StockModel;
import nl.rug.aoop.stockapplication.stock.StockData;
import nl.rug.aoop.stockapplication.stock.Stocks;
import nl.rug.aoop.model.StockDataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestStockManager {

    @Mock
    private Stocks stocks;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConstructor() {
        assertNotNull(stocks);
    }

    @Test
    public void testInitStockModelWithNullStocks() {
        when(stocks.getStocks()).thenReturn(null);

        List<StockDataModel> stockDataModelList = StockManager.initStockModel(stocks);

        assertNotNull(stockDataModelList);
        assertTrue(stockDataModelList.isEmpty());
    }

    @Test
    public void testInitStockModelWithEmptyStocks() {
        when(stocks.getStocks()).thenReturn(Map.of());

        List<StockDataModel> stockDataModelList = StockManager.initStockModel(stocks);

        assertNotNull(stockDataModelList);
        assertTrue(stockDataModelList.isEmpty());
    }

    @Test
    public void testInitStockModelWithValidStocks() {
        StockData stockData1 = new StockData();
        stockData1.setSymbol("AAPL");
        stockData1.setName("Apple Inc.");
        stockData1.setSharesOutstanding(1000.0);
        stockData1.setInitialPrice(150.0);

        StockData stockData2 = new StockData();
        stockData2.setSymbol("GOOGL");
        stockData2.setName("Alphabet Inc.");
        stockData2.setSharesOutstanding(500.0);
        stockData2.setInitialPrice(200.0);

        when(stocks.getStocks()).thenReturn(Map.of("AAPL", stockData1, "GOOGL", stockData2));

        List<StockDataModel> stockDataModelList = StockManager.initStockModel(stocks);
        assertNotNull(stockDataModelList);

        assertNotNull(stockDataModelList);
        assertEquals(2, stockDataModelList.size());

        List<StockDataModel> toRemove = new ArrayList<>();
        for(StockDataModel stockDataModel : stockDataModelList) {
            if(stockDataModel.getName().equals(stockData1.getName()) ||
            stockDataModel.getName().equals(stockData2.getName())) {
                toRemove.add(stockDataModel);
            }
        }

        stockDataModelList.removeAll(toRemove);
        assertTrue(stockDataModelList.isEmpty());
    }
}
