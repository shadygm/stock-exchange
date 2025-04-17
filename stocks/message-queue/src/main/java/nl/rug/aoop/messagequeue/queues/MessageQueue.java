package nl.rug.aoop.messagequeue.queues;

import nl.rug.aoop.messagequeue.message.Message;

/**
 * The message queue data structure that
 * passes messages between the publisher and the
 * consumer.
 */
public interface MessageQueue {

    /**
     * The enqueue method adds a message to the queue.
     * @param message The message to be added.
     */
    void enqueue(Message message);

    /**
     * Removes a method from the queue.
     * @return The message that was removed.
     */
    Message dequeue();

    /**
     * Returns the size of the queue.
     * @return The size of the queue.
     */
    int getSize();
}