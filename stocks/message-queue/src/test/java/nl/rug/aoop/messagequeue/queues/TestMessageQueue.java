package nl.rug.aoop.messagequeue.queues;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.MessageQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks if the methods of the classes which incorporate the
 * MessageQueue interface work correctly.
 * @param <T> The classes which extend the MessageQueue interface.
 */
public abstract class TestMessageQueue <T extends MessageQueue> {
    protected T messageQueue;

    protected abstract T createMessageQueue();

    /**
     * Creates a new instance of the class which implements the
     * MessageQueue interface before each test.
     */
    @BeforeEach
    public void setUp() {
        messageQueue = createMessageQueue();
    }

    /**
     * Test if the constructor of the queues which
     * implement the MessageQueue interface work correctly.
     */
    @Test
    public void testQueueConstructor() {
        assertNotNull(messageQueue);
        assertEquals(0, messageQueue.getSize());
    }

    /**
     * Checks to see if the enqueue method works correctly.
     */
    @Test
    public void testEnqueue() {
        Message msg1 = new Message("header", "body");

        messageQueue.enqueue(msg1);

        assertEquals(1, messageQueue.getSize());
    }

    /**
     * Tests that the enqueue method does not add a null element to the queue.
     */
    @Test
    public void testEnqueueWithNullElement() {
        messageQueue.enqueue(null);
        assertEquals(0, messageQueue.getSize());
    }

    /**
     * Tests that the enqueue method works for a large number of elements.
     */
    @Test
    public void testEnqueueDequeueOneMillionMessages() {
        for (int i = 0; i < 1_000_000; i++) {
            Message msg = new Message("Header" + i, "Body" + i);
            messageQueue.enqueue(msg);
        }

        assertEquals(1_000_000, messageQueue.getSize());

        for (int i = 0; i < 1_000_000; i++) {
            messageQueue.dequeue();
        }

        assertEquals(0, messageQueue.getSize());
    }

    /**
     * Tests the dequeue method to ensure that it
     * works when there are no elements are in it.
     */
    @Test
    public void testDequeueWithNoElement() {
        assertNull(messageQueue.dequeue());
    }

    /**
     * Tests the dequeue method to ensure that it works
     * when a null has been inserted into the queue. Note that
     * it is not possible to insert a null element into the queue.
     */
    @Test
    public void testDequeueWithNullElementEnqueued() {
        messageQueue.enqueue(null);
        assertNull(messageQueue.dequeue());
    }

    /**
     * Checks to see if the dequeue and enqueue method
     * work properly for a single item.
     */
    @Test
    public void testQueueSizeAfterOneElementRemoved() {
        Message msg = new Message("header", "body");

        messageQueue.enqueue(msg);
        assertEquals(1, messageQueue.getSize());
        messageQueue.dequeue();

        assertEquals(0, messageQueue.getSize());
    }

    /**
     * Checks to see if the dequeue and enqueue method
     * work properly for multiple items.
     */
    @Test
    public void testGetSizeMultipleElements() {
        Message message1 = new Message("header", "body");
        Message message2 = new Message("header", "body");
        Message message3 = new Message("header", "body");

        assertEquals(0, messageQueue.getSize());

        messageQueue.enqueue(message3);
        messageQueue.enqueue(message1);
        messageQueue.enqueue(message2);

        assertEquals(3, messageQueue.getSize());

        messageQueue.dequeue();
        assertEquals(2, messageQueue.getSize());
        messageQueue.dequeue();
        assertEquals(1, messageQueue.getSize());
        messageQueue.dequeue();
        assertEquals(0, messageQueue.getSize());
    }
}