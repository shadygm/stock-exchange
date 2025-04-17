package nl.rug.aoop.command;

import java.util.Map;

/**
 * Interface to execute specific interactions i.e. poll/put
 */
public interface Command {

    /**
     * Executes the instruction.
     * @param params The parameters of the message.
     */
    void execute(Map<String, Object> params);
}