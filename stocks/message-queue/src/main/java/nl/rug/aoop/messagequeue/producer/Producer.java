package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.MessageQueue;

/**
 * The publisher of messages that will put
 * messages in the message queue.
 */
public class Producer implements MQProducer {
    private final MessageQueue queue;

    /**
     * Creates a new publisher that will interact with the given message queue.
     * @param queue The message queue that the publisher will interact with.
     */
    public Producer(MessageQueue queue) {
        this.queue = queue;
    }

    /**
     * The publisher will put the given message in the message queue.
     * @param message The message to be added.
     */
    @Override
    public void put(Message message) {
        queue.enqueue(message);
    }
}