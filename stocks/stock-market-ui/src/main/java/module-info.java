module stock.market.ui {
    requires static lombok;
    exports nl.rug.aoop.model;
    exports nl.rug.aoop.initialization;
    requires org.slf4j;
    requires com.google.gson;
    requires java.desktop;
    requires com.formdev.flatlaf;
    requires jdk.httpserver;
    opens nl.rug.aoop.model to com.google.gson;
}