package server.command;

import server.command.hierarchy.Command;

public class CommandExecutor {

    public void executeCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null reference!");
        }
        command.execute();
    }
}
