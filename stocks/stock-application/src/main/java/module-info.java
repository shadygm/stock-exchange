module stockapplication {
    requires static lombok;
    requires org.slf4j;
    requires messagequeue;
    requires networking;
    requires awaitility;
    requires stock.market.ui;
    requires command;
    requires traderapplication;
    requires util;
    opens nl.rug.aoop.stockapplication.stock to com.fasterxml.jackson.databind, org.mockito;
    opens nl.rug.aoop.stockapplication.startup to org.mockito;
    opens nl.rug.aoop.stockapplication.interaction to org.mockito;
    opens nl.rug.aoop.stockapplication.manager to org.mockito;
    opens nl.rug.aoop.stockapplication.loaders to org.mockito;
}