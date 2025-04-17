package nl.rug.aoop.stockapplication.interaction;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.networking.server.ClientHandler;
import nl.rug.aoop.networking.server.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles sending of messages to the traders.
 */
@Slf4j
public class ConnectionManager implements Runnable {
    private final ConcurrentMap<String, ClientHandler> handlerTraderDataMap = new ConcurrentHashMap<>();
    private final List<String> traderId;
    private final Server server;

    /**
     * Constructor for the connection manager.
     * @param traderId list of trader ids
     * @param server The server
     */
    public ConnectionManager(List<String> traderId, Server server) {
        this.traderId = traderId;
        this.server = server;
    }

    /**
     * Maps the id of the trader to the message we want to send to them.
     * @param sending The map of trader ids to messages.
     */
    public void send(Map<String, Message> sending) {
        if(handlerTraderDataMap.isEmpty()) {
            log.info("Map is empty");
            return;
        }

        for (Map.Entry<String, Message> entry : sending.entrySet()) {
            String traderId = entry.getKey();

            if(!handlerTraderDataMap.containsKey(traderId)) {
                log.info("Trader " + traderId + " is not connected.");
                continue;
            }

            ClientHandler clientHandler = handlerTraderDataMap.get(traderId);

            if (clientHandler != null && clientHandler.isConnected() && clientHandler.isRunning()) {
                clientHandler.sendMessageAsync(entry.getValue().convertToJSON());
            }
        }
    }

    @Override
    public void run() {
        Thread buildMap = new Thread(this::buildMap);
        buildMap.start();
        Thread checkDeadClientHandlersThread = new Thread(this::checkDeadClientHandlers);
        checkDeadClientHandlersThread.start();
    }

    private void checkDeadClientHandlers() {
        List<ClientHandler> toRemove = new ArrayList<>();

        while (server.isRunning()) {
            List<ClientHandler> ch = server.getClientHandlers();

            synchronized (handlerTraderDataMap) {
                Iterator<Map.Entry<String, ClientHandler>> iterator = handlerTraderDataMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, ClientHandler> entry = iterator.next();
                    ClientHandler clientHandler = entry.getValue();

                    if (!ch.contains(clientHandler)) {
                        toRemove.add(clientHandler);
                        iterator.remove();
                    }
                }
            }

            if (!toRemove.isEmpty()) {
                log.info("Removed " + toRemove.size() + " dead client handlers.");
                toRemove.clear();
            }
        }
    }

    private void buildMap() {
        int traderIndex = 0;
        ClientHandler mostRecent = null;
        while (server.isRunning()) {
            List<ClientHandler> ch = server.getClientHandlers();
            if(ch.isEmpty()) {
                continue;
            }
            ClientHandler last = ch.get(ch.size() - 1);
            if (last != null && last != mostRecent) {
                mostRecent = last;
                handlerTraderDataMap.put(traderId.get(traderIndex), last);
                traderIndex++;
                if (traderIndex > traderId.size()) {
                    traderIndex = 0;
                    break;
                }
            }
        }
    }
}
