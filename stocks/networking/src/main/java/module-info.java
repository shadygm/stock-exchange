module networking {
    requires static lombok;
    requires org.slf4j;
    requires org.mockito;
    requires messagequeue;
    requires command;
    requires awaitility;
    opens nl.rug.aoop.networking.messagehandlers to org.mockito, stock.application;
    opens nl.rug.aoop.networking.client to org.mockito, stock.application;
    opens nl.rug.aoop.networking.commandhandlers to org.mockito;
    opens nl.rug.aoop.networking.server to org.mockito, stock.application;
    opens nl.rug.aoop.networking.networkproducer to org.mockito, stock.application;
    exports nl.rug.aoop.networking.server;
    exports nl.rug.aoop.networking.client;
    exports nl.rug.aoop.networking.messagehandlers;
    exports nl.rug.aoop.networking.commandhandlers;
    exports nl.rug.aoop.networking.networkproducer;
    exports nl.rug.aoop.networking.commands;
    opens nl.rug.aoop.networking.commands to org.mockito;
}