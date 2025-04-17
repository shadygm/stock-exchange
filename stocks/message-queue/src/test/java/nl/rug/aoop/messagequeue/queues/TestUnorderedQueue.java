package nl.rug.aoop.messagequeue.queues;

import nl.rug.aoop.messagequeue.message.Message;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the UnorderedQueue class for properties
 * which are unique solely to the unorderedQueue class.
 */
public class TestUnorderedQueue extends TestMessageQueue<UnorderedQueue> {

    /**
     * Creates a new UnorderedQueue object.
     * @return A new UnorderedQueue object.
     */
    @Override
    protected UnorderedQueue createMessageQueue() {
        return new UnorderedQueue();
    }

    /**
     * Checks if the correct methods are implemented in the classes.
     */
    @Test
    public void testQueueMethods() {
        List<String> expectedMethodNames = Arrays.asList("enqueue", "dequeue", "getSize");
        Method[] methods = messageQueue.getClass().getDeclaredMethods();

        for (Method method : methods) {
            assertTrue(expectedMethodNames.contains(method.getName()));
        }
    }

    /**
     * Checks that the dequeue method returns the first element
     * that was added to the queue.
     */
    @Test
    public void testDequeueReturnValue() {
        Message msg1 = new Message ("header", "body");
        Message msg2 = new Message ("header", "body");

        messageQueue.enqueue(msg1);
        messageQueue.enqueue(msg2);

        assertEquals(msg1, messageQueue.dequeue());
    }

    /**
     * Tests that the ordering (FIFO) of the queue is preserved.
     */
    @Test
    public void testQueueOrdering() {
        Message message1 = new Message("header", "body");
        Message message2 = new Message("header", "body");
        Message message3 = new Message("header", "body");

        messageQueue.enqueue(message3);
        messageQueue.enqueue(message1);
        messageQueue.enqueue(message2);

        assertEquals(message3, messageQueue.dequeue());
        assertEquals(message1, messageQueue.dequeue());
        assertEquals(message2, messageQueue.dequeue());
    }
}