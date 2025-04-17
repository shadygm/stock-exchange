module traderapplication {
    requires static lombok;
    requires org.slf4j;
    requires com.google.gson;
    requires networking;
    requires awaitility;
    requires stock.market.ui;
    requires command;
    requires util;
    requires messagequeue;
    exports nl.rug.aoop.traderapplication.trader to stockapplication;
    exports nl.rug.aoop.traderapplication.managers;
    exports nl.rug.aoop.traderapplication.loaders to stockapplication;
    exports nl.rug.aoop.traderapplication.order  to stockapplication;
    opens nl.rug.aoop.traderapplication.trader to com.fasterxml.jackson.databind;
    opens nl.rug.aoop.traderapplication.order to com.google.gson;
    opens nl.rug.aoop.traderapplication.bot to org.mockito;
}