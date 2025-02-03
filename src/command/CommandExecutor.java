package command;

import command.hierarchy.Command;

public class CommandExecutor {

    public String executeCommand(Command command) throws Exception {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null reference!");
        }
        return command.execute();
    }
}
