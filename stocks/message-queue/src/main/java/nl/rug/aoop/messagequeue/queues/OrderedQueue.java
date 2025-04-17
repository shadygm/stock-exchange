package nl.rug.aoop.messagequeue.queues;

import nl.rug.aoop.messagequeue.message.Message;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The messages are stored in a sorted map, where the key is the timestamp.
 * They are first sorted by their timestamp, and then by the order in which
 * they were received.
 */
public class OrderedQueue implements MessageQueue {

    private final SortedMap<LocalDateTime, List<Message>> messageMap;

    /**
     * The constructor for the OrderedQueue.
     * Uses a TreeMap to store the messages.
     */
    public OrderedQueue() {
        messageMap = new TreeMap<>();
    }

    /**
     * The message is added to the queue in the order of the timestamp.
     * If there are timestamp collisions, then the messages are added
     * in the order that they were received.
     * @param message The message to be added.
     */
    @Override
    public void enqueue(Message message) {
        if (message != null) {
            LocalDateTime timestamp = message.getTimestamp();
            List<Message> messages = messageMap.get(timestamp);
            if (messages == null) {
                messages = new ArrayList<>();
                messageMap.put(timestamp, messages);
            }
            messages.add(message);
        }
    }

    /**
     * The message with the earliest timestamps removed from the queue.
     * @return The message with the earliest timestamp.
     */
    @Override
    public Message dequeue() {
        if (messageMap.isEmpty()) {
            return null;
        }

        List<Message> messages = messageMap.get(messageMap.firstKey());
        Message removedMessage = messages.remove(0);
        if (messages.isEmpty()) {
            messageMap.remove(messageMap.firstKey());
        }

        return  removedMessage;
    }

    /**
     * Returns the number of messages in the queue.
     * @return The number of messages in the queue.
     */
    @Override
    public int getSize() {
        return messageMap.values().stream().mapToInt(List::size).sum();
    }
}