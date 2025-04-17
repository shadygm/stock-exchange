package nl.rug.aoop.networking.client;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.messagehandlers.MessageHandler;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Client class that connects to a server and sends messages to it.
 */
@Slf4j
public class Client implements Runnable {

    /**
     * The timeout for the socket.
     */
    public static final int TIMEOUT = 7000;

    private final InetSocketAddress address;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(false);
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final MessageHandler messageHandler;

    /**
     * Constructor for the clinet.
     * @param address The address of the server it connects to.
     * @param messageHandler The message handler that handles the messages.
     * @throws IOException If the socket could not connect.
     */
    public Client(InetSocketAddress address, MessageHandler messageHandler) throws IOException {
        this.address = address;
        this.messageHandler = messageHandler;
        initSocket(address);
    }

    /**
     * Initializes the socket.
     * @param address The address of the server it connects to.
     * @throws IOException If the socket could not connect.
     */
    private void initSocket(InetSocketAddress address) throws IOException {
        this.socket = new Socket();
        this.socket.connect(address, TIMEOUT);

        if (!this.socket.isConnected()) {
            log.error("Socket could not connect at port " + address.getPort());
            throw new IOException("Socket could not connect.");
        }

        connected.set(true);

        out = new ObjectOutputStream(this.socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(this.socket.getInputStream());
    }

    /**
     * Checks if the client is running.
     * @return True if the client is running, false otherwise.
     */
    public synchronized boolean isRunning() {
        return running.get();
    }

    /**
     * Checks if the client is connected to the server.
     * @return True if the clien tis connected, false otherwise.
     */
    public synchronized boolean isConnected() {
        return connected.get();
    }

    /**
     * Checks if the socket is bound.
     * @return True if the socket is bound, false otherwise.
     */
    public boolean isBound() {
        return socket.isBound();
    }

    /**
     * Runs the client. Is part of the runnable interface.
     */
    @Override
    public void run() {
        log.info("Starting client on port " + this.address.getPort());
        running.set(true);
    }

    /**
     * Reads a message from the server.
     * @return The message that was read.
     */
    public String readInput() {
        String message = null;
        try {
            log.info("Waiting for message from server");
            message = (String) in.readObject();
        } catch (IOException e) {
            log.error("Error reading string from server", e);
            terminate();
        } catch (ClassNotFoundException e) {
            log.error("Unable to cast object read to String", e);
            terminate();
        }

        if (message != null) {
            log.info("Message received");
            return message;
        }
        terminate();
        return null;
    }

    /**
     * Sends a message to the server.
     * @param messageFromUser The message to be sent.
     * @return True if the message was sent successfully, false otherwise.
     */
    public boolean sendMessageAsync(String messageFromUser) {
        if (messageFromUser == null) {
            log.info("Unable to send message, message is null.");
            return false;
        }
        if (!connected.get() || !running.get()) {
            log.info("Unable to send message, not connected to server.");
            return false;
        }
        Thread senderThread = new Thread(() -> {
            sendMessage(messageFromUser);
        });

        senderThread.start();
        return true;
    }

    /**
     * Sends a message to the server.
     * @param messageFromUser The message to be sent.
     */
    private void sendMessage(String messageFromUser) {
        try {
            if (connected.get()) {
                out.writeObject(messageFromUser);
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
     * Terminates the client and closes the connection.
     */
    public void terminate() {
        if (this.socket.isClosed() || !running.get()) {
            return;
        }
        try {
            connected.set(false);
            running.set(false);
            in.close();
            out.close();
            this.socket.close();
            log.info("Socket closed successfully");
        } catch (IOException e) {
            log.error("Could not close the socket.", e);
        }
    }
}