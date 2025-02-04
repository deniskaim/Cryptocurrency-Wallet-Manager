package command.hierarchy;

import exceptions.command.UnsuccessfulCommandException;

public interface Command {

    String execute() throws UnsuccessfulCommandException;
}
