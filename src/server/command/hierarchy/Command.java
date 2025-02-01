package server.command.hierarchy;

import exceptions.UserException;

public interface Command {

    String execute() throws UserException;
}
