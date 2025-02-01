package server.command;

import exceptions.UserException;
import server.command.hierarchy.Command;

public class CommandExecutor {

    public String executeCommand(Command command) throws UserException {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null reference!");
        }
        return command.execute();
    }
}
