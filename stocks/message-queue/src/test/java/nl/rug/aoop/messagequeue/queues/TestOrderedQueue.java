package nl.rug.aoop.messagequeue.queues;

import nl.rug.aoop.messagequeue.message.Message;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests special properties which are unique to the orderedQueue class.
 */
public class TestOrderedQueue extends TestMessageQueue<OrderedQueue> {

    /**
     * Creates a new instance of the message queue.
     * @return The new instance.
     */
    @Override
    protected OrderedQueue createMessageQueue() {
        return new OrderedQueue();
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
     * Tests to make sure that the ordering properties of
     * the queue are kept when the messages have different timestamps.
     */
    @Test
    public void testQueueEnqueueWithDelay() {
        Message message1 = new Message("header1", "body", LocalDateTime.now());
        waitHelperMethod();
        Message message2 = new Message("header2", "body", LocalDateTime.now());
        waitHelperMethod();
        Message message3 = new Message("header3", "body", LocalDateTime.now());
        
        messageQueue.enqueue(message3);
        messageQueue.enqueue(message1);
        messageQueue.enqueue(message2);

        assertEquals(message1, messageQueue.dequeue());
        assertEquals(message2, messageQueue.dequeue());
        assertEquals(message3, messageQueue.dequeue());
    }

    /**
     * Tests to make sure that the ordering properties of
     * the queue are kept when the messages have the same timestamp.
     */
    @Test
    public void testQueueEnqueueWithoutDelay() {
        LocalDateTime currentTime = LocalDateTime.now();
        Message message1 = new Message("header1", "body", currentTime);
        Message message2 = new Message("header2", "body", currentTime);
        Message message3 = new Message("header3", "body", currentTime);

        messageQueue.enqueue(message3);
        messageQueue.enqueue(message1);
        messageQueue.enqueue(message2);

        assertEquals(message3, messageQueue.dequeue());
        assertEquals(message1, messageQueue.dequeue());
        assertEquals(message2, messageQueue.dequeue());
    }

    private static void waitHelperMethod() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
