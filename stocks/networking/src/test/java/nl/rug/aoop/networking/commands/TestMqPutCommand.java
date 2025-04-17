package nl.rug.aoop.networking.commands;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.ThreadSafeQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the MqPutCommand class.
 */
public class TestMqPutCommand {
    private MqPutCommand mqPutCommand;
    private Map<String, Object> params;
    private Message tempMsg;

    /**
     * Sets up the MqPutCommand before each test.
     */
    @BeforeEach
    public void setUp() {
        mqPutCommand = new MqPutCommand();
        params = new HashMap<>();
        tempMsg = new Message("header", "body");

        params.put("body", tempMsg.convertToJSON());
    }

    /**
     * Tests the constructor.
     */
    @Test
    public void testConstructor() {
        assertNotNull(mqPutCommand);
        assertEquals(mqPutCommand.getClass(), MqPutCommand.class);
    }

    /**
     * Tests whether the put command works.
     */
    @Test
    public void testPutCommand() {
        params.put("header", "MqPut");
        mqPutCommand.execute(params);
        Message dequeueMsg = ThreadSafeQueue.getInstance().dequeue();

        assertEquals(dequeueMsg.getBody(), tempMsg.getBody());
        assertEquals(dequeueMsg.getHeader(), tempMsg.getHeader());
        assertEquals(dequeueMsg.getTimestamp(), tempMsg.getTimestamp());
    }
}