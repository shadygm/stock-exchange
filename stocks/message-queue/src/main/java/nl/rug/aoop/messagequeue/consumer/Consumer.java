package nl.rug.aoop.messagequeue.consumer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.MessageQueue;

/**
 * The consumer reads messages from the queue.
 */
public class Consumer implements MQConsumer {
    private final MessageQueue queue;

    /**
     * The constructor for the subscriber class.
     * @param queue The queue that the subscriber will read from.
     */
    public Consumer(MessageQueue queue) {
        this.queue = queue;
    }

    /**
     * Returns the message that the subscriber read from the queue.
     * @return The message that the subscriber read from the queue.
     * NULL if there are no messages to read.
     */
    @Override
    public Message poll() {
        return queue.dequeue();
    }
}