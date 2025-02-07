package command;

import command.pattern.Command;
import exceptions.command.UnsuccessfulCommandException;

public class CommandExecutor {

    public String executeCommand(Command command) throws UnsuccessfulCommandException {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null reference!");
        }
        return command.execute();
    }
}
