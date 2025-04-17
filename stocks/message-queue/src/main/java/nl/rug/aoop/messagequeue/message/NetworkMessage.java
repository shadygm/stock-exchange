package nl.rug.aoop.messagequeue.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class represents a message structure for communication over a network.
 * It consists of a header and a body, both of which are represented as strings.
 *
 * @param header The header of the network message.
 * @param body   The body of the network message.
 */
public record NetworkMessage(String header, String body) {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(NetworkMessage.class, new NetworkMessageTypeAdapter().nullSafe())
            .create();

    /**
     * Constructor of this class. Creates a NetworkMessage with
     * the specified header and body.
     *
     * @param header The header of the network message.
     * @param body   The body of the network message.
     */
    public NetworkMessage(String header, String body) {
        this.header = header == null ? "" : header;
        this.body = body == null ? "" : body;
    }

    /**
     * Creates a new NetworkMessage for putting a message into a message queue.
     *
     * @param message The message to be put into the queue.
     * @return A NetworkMessage.
     */
    public static NetworkMessage createPutMessage(Message message) {
        String body = message.convertToJSON();
        return new NetworkMessage("MqPut", body);
    }

    /**
     * Converts the NetworkMessage object to its JSON representation.
     *
     * @return The JSON representation of the NetworkMessage.
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * Parses a JSON string into a NetworkMessage object.
     *
     * @param json The JSON string to be parsed.
     * @return A NetworkMessage object created from the provided JSON string.
     */
    public static NetworkMessage fromJson(String json) {
        return GSON.fromJson(json, NetworkMessage.class);
    }
}