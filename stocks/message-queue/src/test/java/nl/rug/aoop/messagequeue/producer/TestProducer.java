package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.MessageQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


/**
 * Tests that the producer works as expected.
 */
@ExtendWith(MockitoExtension.class)
public class TestProducer {
    private Producer producer;
    private MessageQueue mockQueue;

    /**
     * Creates a producer and a mock queue before
     * each test.
     */
    @BeforeEach
    public void setUp() {
        mockQueue = mock(MessageQueue.class);
        producer = new Producer(mockQueue);
    }

    /**
     * Tests the constructor of the producer class.
     */
    @Test
    public void testProducerConstructor() {
        assertNotNull(producer);
    }

    /**
     * Checks if the correct methods are implemented in the classes.
     */
    @Test
    public void testProducerMethods() {
        List<String> expectedMethodNames = List.of("put");
        Method[] methods = producer.getClass().getDeclaredMethods();

        for (Method method : methods) {
            assertTrue(expectedMethodNames.contains(method.getName()));
        }
    }

    /**
     * Tests if the producer puts messages in the queue correctly.
     */
    @Test
    public void testPutting() {
        Message msg1 = new Message("header", "body");

        producer.put(msg1);

        verify(mockQueue, times(1)).enqueue(msg1);
    }
}