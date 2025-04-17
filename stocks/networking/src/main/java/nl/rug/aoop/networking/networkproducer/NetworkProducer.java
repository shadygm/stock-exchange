package nl.rug.aoop.networking.networkproducer;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.messagequeue.producer.MQProducer;
import nl.rug.aoop.networking.client.Client;

/**
 * The producer which puts messages in the queue
 * through the network.
 */
@Slf4j
public class NetworkProducer implements MQProducer {
    private final Client communicator;

    /**
     * Constructor for the network producer.
     * @param communicator The client that wants to send a message.
     */
    public NetworkProducer(Client communicator) {
        this.communicator = communicator;
    }

    /**
     * Puts the message in the queue.
     * @param message The message to put in the queue.
     */
    @Override
    public void put(Message message) {
        if (!communicator.isRunning()) {
            Thread clientThread = new Thread(communicator);
            clientThread.start();
        }
        if (!communicator.isConnected()) {
            log.error("Client is not connected.");
            return;
        }
        NetworkMessage nw = NetworkMessage.createPutMessage(message);
        log.info("Sending message: " + nw.toJson());
        communicator.sendMessageAsync(nw.toJson());
    }
}