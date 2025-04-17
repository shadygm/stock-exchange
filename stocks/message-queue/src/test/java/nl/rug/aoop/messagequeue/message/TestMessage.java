package nl.rug.aoop.messagequeue.message;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the Message class.
 */
@Slf4j
public class TestMessage {

    private Message message;
    private String messageHeader;
    private String messageBody;

    /**
     * Creates a message with a header and a body before each test.
     */
    @BeforeEach
    public void setUp() {
        messageHeader = "header";
        messageBody = "body";
        message = new Message(messageHeader, messageBody);
    }

    /**
     * Tests the constructor of the Message class.
     */
    @Test
    public void testMessageConstructor() {
        assertEquals(messageHeader, message.getHeader());
        assertEquals(messageBody, message.getBody());
        assertNotNull(message.getTimestamp());
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
        List<Field> fields = List.of(Message.class.getDeclaredFields());
        fields.forEach(field -> assertTrue(Modifier.isFinal(field.getModifiers()), field.getName() + " is not final"));
    }

    /**
     * Tests whether the message can be converted to JSON succesfully.
     */
    @Test
    public void testConvertToJson() {
        String json = message.convertToJSON();
        log.info(json);
        assertTrue(json.contains(messageBody));
        assertTrue(json.contains(messageHeader));
    }

    /**
     * Tests whether the message can be converted from JSON successfully.
     */
    @Test
    public void testConvertFromJson() {
        LocalDateTime msgTime = message.getTimestamp();
        String json = message.convertToJSON();

        assertTrue(json.contains(messageBody));
        assertTrue(json.contains(messageHeader));
        assertTrue(json.contains(msgTime.toString()));

        log.info(json);

        Message newMsg = Message.convertFromJSON(json);
        assertEquals(messageBody, newMsg.getBody());
        assertEquals(messageHeader, newMsg.getHeader());
        assertEquals(msgTime, newMsg.getTimestamp());
    }
}