package nl.rug.aoop.networking.messagehandlers;

import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for converting a message to
 * a NetworkMessage and passing it to the CommandHandler.
 */
public class ConvertToMessageHandler implements MessageHandler {
    private final CommandHandler commandHandler;

    /**
     * Constructor of the class.
     *
     * @param commandHandler The CommandHandler to which the message will be passed.
     */
    public ConvertToMessageHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * This method converts a message to a NetworkMessage and passes it
     * to the CommandHandler.
     *
     * @param message The message to be handled.
     */
    @Override
    public void handleMessage(String message) {
        NetworkMessage newMsg = NetworkMessage.fromJson(message);
        Map<String, Object> params = new HashMap<>();

        params.put("header", newMsg.header());
        params.put("body", newMsg.body());
        params.put("messageHandler", this);

        commandHandler.execute(params);
    }
}