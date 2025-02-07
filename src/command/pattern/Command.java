package command.pattern;

import exceptions.command.UnsuccessfulCommandException;

public interface Command {

    String execute() throws UnsuccessfulCommandException;
}
