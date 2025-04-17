package nl.rug.aoop.networking.networksproducer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.messagequeue.producer.MQProducer;
import nl.rug.aoop.networking.client.Client;
import nl.rug.aoop.networking.commandhandlers.GenericCommandHandler;
import nl.rug.aoop.networking.messagehandlers.ConvertToMessageHandler;
import nl.rug.aoop.networking.messagehandlers.MessageHandler;
import nl.rug.aoop.networking.networkproducer.NetworkProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import java.io.IOException;
import static org.mockito.Mockito.verify;

/**
 * Tests for the NetworkProducer class.
 */
public class TestNetworkProducer {
    private MQProducer networkProducer;
    private Client client;

    /**
     * Sets up the NetworkProducer before each test.
     * @throws IOException if the client cannot connect to the server.
     */
    @BeforeEach
    public void setUp() throws IOException {
        MessageHandler messageHandler = new ConvertToMessageHandler(new GenericCommandHandler());
        client = Mockito.mock(Client.class);

        networkProducer = new NetworkProducer(client);
    }

    /**
     * Tests the constructor.
     */
    @Test
    public void testConstructor() {
        assertNotNull(networkProducer);
        assertEquals(networkProducer.getClass(), NetworkProducer.class);
    }

    /**
     * Tests whether the put method sends a message to the server.
     */
    @Test
    public void testPut() {
        Message testMessage = new Message("Test Header", "Test Body");
        NetworkMessage nw = NetworkMessage.createPutMessage(testMessage);
        when(client.isConnected()).thenReturn(true);
        networkProducer.put(testMessage);
        verify(client).sendMessageAsync(nw.toJson());
    }
}
