package nl.rug.aoop.networking.commandhandlers;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.ThreadSafeQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for the GenericCommand class.
 */
@Slf4j
public class TestGenericCommandHandler {
    private GenericCommandHandler genericCommandHandler;
    private Map<String, Object> params;
    private Message tempMsg;

    /**
     * Sets up the GenericCommand before each test.
     */
    @BeforeEach
    public void setUp() {
        params = new HashMap<>();
        tempMsg = new Message("header", "body");
        log.info("Message body: " + tempMsg.convertToJSON());
        params.put("body", tempMsg.convertToJSON());
        genericCommandHandler = new GenericCommandHandler();
    }

    /**
     * Tests whether the constructor adds all the commands to the command map.
     */
    @Test
    public void testConstructor() {
        assertNotNull(genericCommandHandler);
        assertTrue(genericCommandHandler.getCommand().containsKey("MqPut"));
    }

    /**
     * Tests whether the put command works.
     */
    @Test
    public void testPutCommandWorks() {
        params.put("header", "MqPut");
        genericCommandHandler.execute(params);
        Message dequeueMsg = ThreadSafeQueue.getInstance().dequeue();

        assertEquals(dequeueMsg.getBody(), tempMsg.getBody());
        assertEquals(dequeueMsg.getHeader(), tempMsg.getHeader());
        assertEquals(dequeueMsg.getTimestamp(), tempMsg.getTimestamp());
    }
}