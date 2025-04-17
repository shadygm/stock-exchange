package nl.rug.aoop.messagequeue.message;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * This class is used to convert a message to a JSON string and vice versa.
 */
@Slf4j
public final class MessageTypeAdapter extends TypeAdapter<Message> {

    /**
     * The names of the body field in the JSON string.
     */
    public static final String BODY = "body";

    /**
     * The names of the header field in the JSON string.
     */
    public static final String HEADER = "header";

    /**
     * The names of the time related field in the JSON string.
     */
    public static final String TIME_FIELD = "time";

    /**
     * Reads the JSON string and converts it to a message.
     * @param reader The JSON reader.
     * @return The message that was read.
     * @throws IOException If the message could not be read.
     */
    @Override
    public Message read(JsonReader reader) throws IOException {
        reader.beginObject();
        Message msg;

        try {
            msg = readingJSONString(reader, null, null, null);
        } catch (IOException e) {
            log.error("There was an error reading message from JSON");
            throw new IOException("Error reading message from JSON");
        }

        if (msg == null) {
            log.error("There was an error reading message from JSON");
            throw new IOException("Error reading message from JSON");
        } else {
            return msg;
        }
    }

    private Message readingJSONString(JsonReader reader, String header, String body,
                                   LocalDateTime time) throws IOException {
        while (reader.hasNext()) {
            String fieldName = reader.nextName();
            if (fieldName == null) {
                continue;
            }

            switch (fieldName) {
                case BODY:
                    body = reader.nextString();
                    break;
                case HEADER:
                    header = reader.nextString();
                    break;
                case TIME_FIELD:
                    time = LocalDateTime.parse(reader.nextString());
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        if (body == null || header == null || time == null) {
            log.error("There was an error reading message from JSON");
            throw new IOException("Error reading message from JSON");
        }
        return new Message(header, body, time);
    }

    /**
     * Writes the message to a JSON string.
     * @param writer The JSON writer.
     * @param message The message to be written.
     * @throws IOException If the message could not be written.
     */
    @Override
    public void write(JsonWriter writer, Message message) throws IOException {
        writer.beginObject();
        writer.name(BODY);
        writer.value(message.getBody());
        writer.name(HEADER);
        writer.value(message.getHeader());
        writer.name(TIME_FIELD);
        writer.value(message.getTimestamp().toString());
        writer.endObject();
    }
}