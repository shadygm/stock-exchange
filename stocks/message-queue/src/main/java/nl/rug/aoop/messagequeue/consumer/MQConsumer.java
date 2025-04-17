package nl.rug.aoop.messagequeue.consumer;

import nl.rug.aoop.messagequeue.message.Message;

/**
 * The consumer which reads messages
 * from the message queue.
 */
public interface MQConsumer {

    /**
     * Polls the queue for the top most message.
     * @return The next message in the queue.
     */
    Message poll();
}