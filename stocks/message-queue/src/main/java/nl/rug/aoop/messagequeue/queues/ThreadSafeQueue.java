package nl.rug.aoop.messagequeue.queues;

import lombok.Getter;
import nl.rug.aoop.messagequeue.message.Message;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Thread-safe implementation of an ordered message queue.
 * Ordering is done first by the timestamp of the messages
 * and second by LIFO.
 */
public  class ThreadSafeQueue implements MessageQueue {
    @Getter
    private static volatile ThreadSafeQueue instance = new ThreadSafeQueue();
    private final BlockingQueue<Message> priorityBlockingQueue;

    /**
     * Constructor of the ThreadSafeQueue.
     */
    private ThreadSafeQueue() {
        priorityBlockingQueue = new PriorityBlockingQueue<>();
        instance = this;
    }

    /**
     * Enqueue method adds a message to the queue.
     * Elements are ordered first by the timestamp
     * of the messages, and second by LIFO.
     * @param message The message to be added.
     */
    @Override
    public void enqueue(Message message) {
        if (message != null) {
            try {
                priorityBlockingQueue.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The dequeue method removes the head of the queue and
     * returns it.
     * @return The message (from the head of the queue)
     * that was removed from the queue.
     */
    @Override
    public Message dequeue() {
        return  priorityBlockingQueue.poll();
    }

    /**
     * Gets the number of elements in the queue.
     * @return the size of the queue.
     */
    @Override
    public int getSize() {
        return priorityBlockingQueue.size();
    }

    /**
     * Clears the queue.
     */
    public void clear() {
        priorityBlockingQueue.clear();
    }
}