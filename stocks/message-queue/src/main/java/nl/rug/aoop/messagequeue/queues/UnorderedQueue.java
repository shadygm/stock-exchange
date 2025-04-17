package nl.rug.aoop.messagequeue.queues;

import nl.rug.aoop.messagequeue.message.Message;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The UnorderedQueue class implements the MessageQueue interface.
 * The messages are stored in a FIFO queue.
 */
public class UnorderedQueue implements MessageQueue {

    private final Queue<Message> unorderedQueue;

    /**
     * This constructor creates a new LinkedLIst
     * in which the messages are stored.
     */
    public UnorderedQueue() {
        unorderedQueue = new LinkedList<>();
    }

    /**
     * The enqueue method adds a message to the queue.
     * @param message The message to be added to the queue.
     */
    @Override
    public void enqueue(Message message) {
        if (message == null) {
            return;
        }
        unorderedQueue.add(message);
    }

    /**
     * The dequeue method removes a message from the queue.
     * @return The message that was removed from the queue.
     */
    @Override
    public Message dequeue() {
        return unorderedQueue.poll();
    }

    /**
     * The getSize method returns the number of messages in the queue.
     * @return The number of messages in the queue.
     */
    @Override
    public int getSize() {
        return unorderedQueue.size();
    }
}