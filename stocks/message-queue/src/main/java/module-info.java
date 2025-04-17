module messagequeue {
    requires static lombok;
    requires com.google.gson;
    requires org.slf4j;
    requires org.mockito;
    exports nl.rug.aoop.messagequeue.producer to networking, traderapplication;
    exports nl.rug.aoop.messagequeue.message to networking, stockapplication, traderapplication;
    exports nl.rug.aoop.messagequeue.queues to networking, org.mockito, stockapplication;
    exports nl.rug.aoop.messagequeue.consumer to stockapplication;
    opens nl.rug.aoop.messagequeue.consumer to org.mockito;
    opens nl.rug.aoop.messagequeue.producer to org.mockito;
    opens nl.rug.aoop.messagequeue.message to org.mockito;
}