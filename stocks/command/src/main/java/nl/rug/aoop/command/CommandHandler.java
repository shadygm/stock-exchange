package nl.rug.aoop.command;

import java.util.Map;

/**
 * Interface to dictate what commands should be executed.
 */
public interface CommandHandler {

    /**
     * Executes the command.
     * @param params The parameters that the command needs to execute.
     */
    void execute(Map<String, Object> params);
}