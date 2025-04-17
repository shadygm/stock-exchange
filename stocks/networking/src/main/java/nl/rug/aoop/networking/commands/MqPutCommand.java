package nl.rug.aoop.networking.commands;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.queues.MessageQueue;
import nl.rug.aoop.messagequeue.queues.ThreadSafeQueue;
import java.util.Map;

/**
 * Command that puts a message on the message queue.
 */
@Slf4j
public class MqPutCommand implements Command {

    /**
     * The message queue.
     */
    private static final MessageQueue MQ = ThreadSafeQueue.getInstance();

    /**
     * Puts the message on the message queue.
     * @param params The parameters of the message.
     */
    @Override
    public void execute(Map<String, Object> params) {
        String body = (String) params.get("body");
        log.info("Message body: " + body);
        Message msg = Message.convertFromJSON(body);
        log.info("Message body: " + msg.getBody());
        log.info("Message header: " + msg.getHeader());
        MQ.enqueue(msg);
    }
}