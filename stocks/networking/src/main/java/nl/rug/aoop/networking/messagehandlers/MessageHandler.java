package nl.rug.aoop.networking.messagehandlers;

/**
 * Interface for message handlers.
 */
public interface MessageHandler {

    /**
     * Handles the message.
     * @param message The message to be handled.
     */
    void handleMessage(String message);
}