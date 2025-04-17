package nl.rug.aoop.messagequeue.queues;

import nl.rug.aoop.messagequeue.message.Message;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests special properties which are unique to the threadSafe class.
 */
public class TestThreadSafeQueue extends TestMessageQueue<ThreadSafeQueue>{

    /**
     * Creates a new instance of ThreadSafeQueue class.
     * @return the new instance of ThreadSafeQueue class.
     */
    @Override
    protected ThreadSafeQueue createMessageQueue() {
        ThreadSafeQueue messageQueue = ThreadSafeQueue.getInstance();
        messageQueue.clear();
        return messageQueue;
    }

    /**
     * Checks if the correct methods are implemented in the classes.
     */
    @Test
    public void testQueueMethods() {
        List<String> expectedMethodNames = Arrays.asList("enqueue", "dequeue", "getSize", "getInstance", "clear");
        Method[] methods = messageQueue.getClass().getDeclaredMethods();

        for (Method method : methods) {
            assertTrue(expectedMethodNames.contains(method.getName()));
        }
    }

    /**
     * Checks if the queue is empty after creation with delay
     * between the creation of each message.
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
     * Checks if the queue is empty after creation without
     * delay between the creation of each message.
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
        assertEquals(message2, messageQueue.dequeue());
        assertEquals(message1, messageQueue.dequeue());
    }

    /**
     * Helper method that makes JAVA wait for 1 second.
     */
    private static void waitHelperMethod() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}