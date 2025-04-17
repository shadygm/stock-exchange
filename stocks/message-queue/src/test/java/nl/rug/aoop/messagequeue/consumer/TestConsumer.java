package nl.rug.aoop.messagequeue.consumer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.MessageQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Method;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the consumer class.
 */
@ExtendWith(MockitoExtension.class)
public class TestConsumer {
    private Consumer consumer;
    private MessageQueue mockQueue;

    /**
     * Creates a consumer and a mock queue before
     * each test.
     */
    @BeforeEach
    public void setUp() {
        mockQueue = mock(MessageQueue.class);
        consumer = new Consumer(mockQueue);
    }

    /**
     * Tests the constructor of the consumer class.
     */
    @Test
    public void testConsumerConstructor() {
        assertNotNull(consumer);
    }

    /**
     * Checks if the correct methods are implemented in the classes.
     */
    @Test
    public void testConsumerMethods() {
        List<String> expectedMethodNames = List.of("poll");
        Method[] methods = consumer.getClass().getDeclaredMethods();

        for (Method method : methods) {
            assertTrue(expectedMethodNames.contains(method.getName()));
        }
    }

    /**
     * Tests if the consumer polls the queue correctly.
     */
    @Test
    public void testPolling() {
        Message msg1 = new Message("header", "body");
        Message msg2 = new Message("header", "body");

        when(mockQueue.dequeue()).thenReturn(msg1, msg2, null);

        assertEquals(msg1, consumer.poll());
        assertEquals(msg2, consumer.poll());
        assertNull(consumer.poll());
    }
}