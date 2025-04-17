package nl.rug.aoop.messagequeue.message;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import java.time.LocalDateTime;
import com.google.gson.Gson;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

/**
 * This class describes a message that is to be stored and
 * passed from publisher to consumer through the
 * message queue data structure.
 */
@Slf4j @Getter
public class Message implements Comparable<Message> {
    private final String header;
    private final String body;
    private final LocalDateTime timestamp;

    /**
     * The Gson object used to convert the message to JSON.
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageTypeAdapter().nullSafe())
            .create();

    /**
     * Constructor for a message.
     * @param header The header of the message.
     * @param body   The body of the message.
     */
    public Message(String header, String body) {
        this.header = header == null ? "" : header;
        this.body = body == null ? "" : body;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor for a message with a custom time.
     * @param header The header of the message.
     * @param body The body of the message.
     * @param currentTime The time the message was created.
     */
    public Message(String header, String body, LocalDateTime currentTime) {
        this.header = header == null ? "" : header;
        this.body = body == null ? "" : body;
        this.timestamp = currentTime;
    }

    /**
     * Converts the message to a JSON string.
     * @return The current message in JSON format.
     */
    @Synchronized
    public String convertToJSON() {
        return GSON.toJson(this);
    }

    /**
     * Converts a JSON string to a message.
     * @param json The JSON string to be converted.
     * @return The message that was converted.
     * @throws IllegalArgumentException If the JSON string is invalid.
     */
    public static Message convertFromJSON(String json) throws IllegalArgumentException {
        if (json == null || json.isEmpty()) {
            log.error("Invalid JSON: null or empty");
            throw new IllegalArgumentException("Invalid JSON: null or empty");
        }
        return GSON.fromJson(json, Message.class);
    }

    /**
     * Compares the message timestamp to one another for
     * ordering in the ThreadSafeQueue.
     * @param otherMessage the object to be compared.
     * @return an integer representing the ordering of the two objects.
     */
    @Override
    public int compareTo(Message otherMessage) {
        return this.timestamp.compareTo(otherMessage.getTimestamp());
    }
}