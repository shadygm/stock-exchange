package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;

/**
 * Interface for a message producer
 * for the message queue.
 */
public interface MQProducer {

    /**
     * Puts the message in the queue.
     * @param message The message to put in the queue.
     */
    void put(Message message);
}