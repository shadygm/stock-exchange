package nl.rug.aoop.messagequeue.message;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

/**
 * This class is used to convert a NetworkMessage to JSON and vice versa.
 */
@Slf4j
public class NetworkMessageTypeAdapter extends TypeAdapter<NetworkMessage> {

    /**
     * The JSON header field name.
     */
    public static final String HEADER = "header";

    /**
     * The JSON body field name.
     */
    public static final String BODY = "body";

    /**
     * Reads a NetworkMessage from a JSON stream.
     *
     * @param reader The JsonReader to read from.
     * @return The deserialized NetworkMessage.
     * @throws IOException If an I/O error occurs or the JSON data is invalid.
     */
    @Override
    public NetworkMessage read(JsonReader reader) throws IOException {
        reader.beginObject();
        String header = null;
        String body = null;

        while (reader.hasNext()) {
            String fieldName = reader.nextName();
            if (fieldName == null) {
                continue;
            }

            switch (fieldName) {
                case HEADER -> {
                    header = reader.nextString();
                }
                case BODY -> {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(reader);
                    body = jsonElement.toString();
                }
                default -> reader.skipValue();
            }
        }
        reader.endObject();
        if (header == null || body == null) {
            log.error("Error reading NetworkMessage from JSON");
            throw new IOException("Error reading NetworkMessage from JSON");
        }
        return new NetworkMessage(header, body);
    }

    /**
     * Writes a NetworkMessage to a JSON stream.
     *
     * @param writer          The JsonWriter to write to.
     * @param networkMessage  The NetworkMessage to serialize.
     * @throws IOException    If an I/O error occurs during writing.
     */
    @Override
    public void write(JsonWriter writer, NetworkMessage networkMessage) throws IOException {
        writer.beginObject();
        writer.name(HEADER).value(networkMessage.header());

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(networkMessage.body());
        writer.name(BODY);
        new Gson().toJson(jsonElement, writer);

        writer.endObject();
    }
}