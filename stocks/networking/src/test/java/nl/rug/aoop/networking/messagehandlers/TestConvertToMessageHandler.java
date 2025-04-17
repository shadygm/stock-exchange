package nl.rug.aoop.networking.messagehandlers;

import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Tests for the ConvertToMessageHandler class.
 */
public class TestConvertToMessageHandler {
    private CommandHandler commandHandler;
    private ConvertToMessageHandler messageHandler;

    /**
     * Sets up the ConvertToMessageHandler before each test.
     */
    @BeforeEach
    public void setUp() {
        commandHandler = Mockito.mock(CommandHandler.class);
        messageHandler = new ConvertToMessageHandler(commandHandler);
    }

    /**
     * Tests the constructor.
     */
    @Test
    public void testConstructor() {
        assertNotNull(messageHandler);
        assertEquals(messageHandler.getClass(), ConvertToMessageHandler.class);
    }

    /**
     * Tests whether the handleMessage method converts the message and executes the command.
     */
    @Test
    public void testValidExecution() {
        Message message = new Message("Header", "Body");
        NetworkMessage networkMessage = NetworkMessage.createPutMessage(message);

        Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put("header", networkMessage.header());
        expectedParams.put("body", networkMessage.body());
        expectedParams.put("messageHandler", messageHandler);

        messageHandler.handleMessage(networkMessage.toJson());

        verify(commandHandler).execute(expectedParams);
    }
}