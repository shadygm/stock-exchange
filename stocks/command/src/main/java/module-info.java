module command {
    requires static lombok;
    requires org.slf4j;
    exports nl.rug.aoop.command to networking, org.mockito, stock.application;
}