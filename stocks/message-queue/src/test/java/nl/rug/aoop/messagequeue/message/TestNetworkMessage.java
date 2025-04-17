package nl.rug.aoop.messagequeue.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the Message class.
 */
@Slf4j
public class TestNetworkMessage {

    private Message message;
    private String messageHeader;
    private String messageBody;
    private String command = "MqPut";
    private final JsonParser parser = new JsonParser();
    private NetworkMessage networkMessage;

    /**
     * Creates a message with a header and a body before each test.
     */
    @BeforeEach
    public void setUp() {
        messageHeader = "header";
        messageBody = "body";
        message = new Message(messageHeader, messageBody);
        networkMessage = new NetworkMessage(command, message.convertToJSON());
    }

    /**
     * Tests the constructor of the Message class.
     */
    @Test
    public void testMessageConstructor() {
        assertEquals(command, networkMessage.header());
        assertEquals(message.convertToJSON(), networkMessage.body());
    }

    /**
     * Tests to make sure that the header and body of the message
     * cannot be initialized to null.
     */
    @Test
    public void testNullHeaderBody() {
        Message nullHeader = new Message(null, messageBody);
        Message nullBody = new Message(messageHeader, null);

        assertEquals("", nullHeader.getHeader());
        assertEquals("", nullBody.getBody());
    }

    /**
     * Tests to make sure that fields in the Message class
     * cannot be changed.
     */
    @Test
    public void testMessageImmutable() {
        List<Field> fields = List.of(NetworkMessage.class.getDeclaredFields());
        fields.forEach(field -> assertTrue(Modifier.isFinal(field.getModifiers()), field.getName() + " is not final"));
    }

    /**
     * Tests whether the message can be converted to JSON succesfully.
     */
    @Test
    public void testConvertToJson() {
        String json = networkMessage.toJson();
        log.info(json);
        String msgJson = message.convertToJSON();
        log.info(msgJson);
        assertTrue(json.contains(msgJson));
        assertTrue(json.contains(command));
    }

    /**
     * Tests whether the message can be converted from JSON succesfully.
     */
    @Test
    public void testConvertFromJson() {
        String json = networkMessage.toJson();

        log.info(json);

        NetworkMessage newMsg = NetworkMessage.fromJson(json);
        assertEquals(networkMessage.body(), newMsg.body());
        assertEquals(command, newMsg.header());

        Message msg = Message.convertFromJSON(newMsg.body());
        log.info(msg.getBody());
        log.info(msg.getHeader());
        assertEquals(messageBody, msg.getBody());
        assertEquals(messageHeader, msg.getHeader());

    }
}