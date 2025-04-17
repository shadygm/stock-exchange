package nl.rug.aoop.networking.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.messagehandlers.MessageHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Client handler that handles the communication
 * from the server with a client.
 */
@Slf4j
public class ClientHandler implements Runnable {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Socket socket;
    @Getter
    private final MessageHandler messageHandler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean connected = new AtomicBoolean(false);

    /**
     * Constructor for the client handler.
     *
     * @param socket         The socket of the client.
     * @param messageHandler The message handler that handles the messages.
     * @throws IOException If the socket could not connect.
     */
    public ClientHandler(Socket socket, MessageHandler messageHandler) throws IOException {
        this.socket = socket;
        this.messageHandler = messageHandler;

        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        out.flush();
        this.in = new ObjectInputStream(this.socket.getInputStream());
        connected.set(true);
    }

    /**
     * Sends a message to the client.
     *
     * @return True if the message was sent successfully, false otherwise.
     */
    public boolean isRunning() {
        return running.get();
    }

    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Runs the client handler.
     */
    @Override
    public void run() {
        running.set(true);
        new Thread(this::runUntilTermination).start();
    }

    /**
     * Runs the client handler until the connection is terminated.
     */
    private void runUntilTermination() {
        while (running.get() && !socket.isClosed()) {
            try {
                String messageFromUser = (String) in.readObject();
                if (messageFromUser == null) {
                    break;
                }
                messageHandler.handleMessage(messageFromUser);
                log.info("Message from user: " + messageFromUser);
            } catch (IOException e) {
                log.error("Error reading string from user.", e);
                break;
            } catch (ClassNotFoundException e) {
                log.error("Could not find class.", e);
                break;
            }
        }
        terminate();
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message to be sent.
     * @return True if the message was sent successfully, false otherwise.
     */
    public boolean sendMessageAsync(String message) {
        if (message == null) {
            log.info("Unable to send message, message is null");
            return false;
        } else if (!this.isRunning()) {
            log.info("Unable to send message, not running.");
            return false;
        }

        Thread senderThread = new Thread(() -> {
            sendMessage(message);
        });
        senderThread.start();
        try {
            senderThread.join(1000);
        } catch (InterruptedException e) {
            log.error("Could not join threads.", e);
            return false;
        }
        return true;
    }

    private void sendMessage(String message) {
        try {
            if (this.isRunning()) {
                out.writeObject(message);
                out.flush();
            } else {
                log.info("Unable to send message, not connected to server.");
            }
        } catch (IOException e) {
            log.error("Error sending message to server", e);
            terminate();
        }
    }

    /**
     * Terminates the client handler connection.
     */
    public void terminate() {
        if (this.socket.isClosed() || !running.get()) {
            return;
        }

        try {
            running.set(false);
            connected.set(false);
            in.close();
            out.close();
            this.socket.close();
            log.info("Socket  closed successfully");
        } catch (IOException e) {
            log.error("Could not close the socket.", e);
        }
    }
}