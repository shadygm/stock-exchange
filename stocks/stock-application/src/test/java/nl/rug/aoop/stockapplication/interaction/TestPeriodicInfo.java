package nl.rug.aoop.stockapplication.interaction;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TestPeriodicInfo {
    private ConnectionManager mockConnectionManager;
    private PeriodicInfo periodicInfo;
    private List<StockDataModel> stockDataModels;
    private List<TraderDataModel> traderDataModels;

    private static final String AAPL = "AAPL";
    private static final String APPLE_INC = "Apple Inc";

    private static final String GOOG = "GOOG";
    private static final String ALPHABET_INC = "Alphabet Inc";

    private static final String TRADER1 = "Trader1";
    private static final String BEN_DOVER = "Ben Dover";

    private static final String TRADER2 = "Trader2";
    private static final String JACK_INGOFF = "Jack Ingoff";

    @BeforeEach
    public void setUp() {
        mockConnectionManager = Mockito.mock(ConnectionManager.class);
        periodicInfo = new PeriodicInfo(mockConnectionManager);
        initLists();
    }

    private void initLists() {
        stockDataModels = new ArrayList<>();
        StockDataModel stock1 = new StockModel(AAPL, APPLE_INC, (long) 150.0, 10.0);
        StockDataModel stock2 = new StockModel(GOOG, ALPHABET_INC, (long) 2000.0, 5.0);
        stockDataModels.add(stock1);
        stockDataModels.add(stock2);

        traderDataModels = new ArrayList<>();
        TraderDataModel trader1 = new TraderModel(TRADER1, BEN_DOVER, 1234, initMap());
        TraderDataModel trader2 = new TraderModel(TRADER2, JACK_INGOFF, 5678, initMap());
        traderDataModels.add(trader1);
        traderDataModels.add(trader2);
    }
    @AfterEach
    public void tearDown() {
        stockDataModels.clear();
        stockDataModels = null;
        traderDataModels.clear();
        traderDataModels = null;
    }

    private Map<String, Double> initMap() {
        Map<String, Double> ownedStocks = new HashMap<>();
        ownedStocks.put(AAPL, 10.0);
        ownedStocks.put(GOOG, 5.0);
        return ownedStocks;
    }

    @Test
    public void testConstructor() {
        assertNotNull(periodicInfo);
    }

    @Test
    public void testSending() {
        periodicInfo.sending(stockDataModels, traderDataModels);
        ArgumentCaptor<Map<String, Message>> messageCaptor = forClass(Map.class);
        verify(mockConnectionManager, times(2)).send(messageCaptor.capture());

        List<Map<String, Message>> capturedMessages = messageCaptor.getAllValues();


        Message stocksMessage = capturedMessages.get(0).get(traderDataModels.get(0).getId());
        assertEquals(PeriodicInfo.INFO_STOCK, stocksMessage.getHeader());
        assertEquals(StockExchangeModel.getINSTANCE().convertStockToJSON(), stocksMessage.getBody());

        Message traderMessage = capturedMessages.get(1).get(traderDataModels.get(1).getId());
        assertEquals(PeriodicInfo.INFO_TRADER, traderMessage.getHeader());
        assertEquals(StockExchangeModel.getINSTANCE().convertTraderToJSON(traderDataModels.get(1)), traderMessage.getBody());
    }
}
