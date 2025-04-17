package nl.rug.aoop.networking.server;

import nl.rug.aoop.networking.commandhandlers.GenericCommandHandler;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.messagehandlers.ConvertToMessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server class that accepts connections from clients.
 */
@Slf4j
public class Server implements Runnable {

    private final int port;
    private final ServerSocket serverSocket;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ExecutorService executorService;
    private final ConvertToMessageHandler msghandler = new ConvertToMessageHandler(new GenericCommandHandler());
    private final List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
    private AtomicInteger numConnectedClient = new AtomicInteger(0);

    /**
     * Constructor for the server.
     * @param port The port the server is running on.
     * @throws IOException If the server could not be created.
     */
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        this.port = port;
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Returns the port the server is running on.
     * @return The port the server is running on.
     */
    public int getPort() {
        return this.serverSocket.getLocalPort();
    }

    /**
     * Runs the server.
     */
    @Override
    public void run() {
        running.set(true);
        new Thread(this::checkForDeadClients).start();
        log.info("Starting server on port " + this.getPort());
        while (running.get()) {
            try {
                Socket socket = this.serverSocket.accept();
                log.info("New connection from client");
                ClientHandler clientHandler = new ClientHandler(socket, msghandler);
                clientHandlers.add(clientHandler);
                this.executorService.submit(clientHandler);
                numConnectedClient.incrementAndGet();
            } catch (IOException e) {
                log.error("Socket cannot accept connection : ", e);
            }
        }
    }

    public int getNumConnectedClient() {
        return numConnectedClient.get();
    }

    /**
     * Checks for dead clients and removes them from the
     * list of clients.
     */
    private void checkForDeadClients() {
        while (running.get()) {
            List<ClientHandler> deadClients = new ArrayList<>();
            for (ClientHandler clientHandler : clientHandlers) {
                boolean passed = awaitConnection(clientHandler);

                if (!passed && !clientHandler.isRunning()) {
                    deadClients.add(clientHandler);
                }
            }

            if (!deadClients.isEmpty()) {
                log.info("Removing " + deadClients.size() + " dead clients");
                clientHandlers.removeAll(deadClients);
                numConnectedClient.set(clientHandlers.size());
            }
        }
    }

    private static boolean awaitConnection(ClientHandler clientHandler) {
        long startTime = System.currentTimeMillis();
        long timeout = 100;

        while (System.currentTimeMillis() - startTime < timeout) {
            if (clientHandler.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    /**
     * Terminates the server.
     */
    public void terminate() {
        if (!this.isRunning()) {
            return;
        }
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.terminate();
        }
        running.set(false);
        this.executorService.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("Unable to close the server socket. ", e);
        }
    }

    /**
     * Returns whether the server is running or not.
     *
     * @return Whether the server is running or not.
     */
    public boolean isRunning() {
        return this.running.get();
    }
}