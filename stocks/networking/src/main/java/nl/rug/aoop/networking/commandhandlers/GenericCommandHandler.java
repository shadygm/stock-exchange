package nl.rug.aoop.networking.commandhandlers;

import nl.rug.aoop.networking.commands.MqPutCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.command.CommandHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic command handler class handles all the commands.
 */
@Slf4j
public class GenericCommandHandler implements CommandHandler {
    @Getter
    private final Map<String, Command> command = new HashMap<>();

    /**
     * Constructor of the class. Adds all the necessary commands
     * to the command map used.
     */
    public GenericCommandHandler() {
        command.put("MqPut", new MqPutCommand());
    }

    /**
     * Executes the command that is specified in the header of the message.
     * @param params The parameters that the command needs to execute.
     */
    public void execute(Map<String, Object> params) {
        String header = (String)params.get("header");

        if (command.containsKey(header)) {
            command.get(header).execute(params);
        } else {
            log.info("Command not found");
        }
    }
}