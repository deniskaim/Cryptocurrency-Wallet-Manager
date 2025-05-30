package command.pattern;

import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import user.User;

import java.nio.channels.SelectionKey;

public class LogOutCommand implements Command {

    private final SelectionKey selectionKey;
    private static final String SUCCESSFUL_MESSAGE = "You have successfully logged out!";

    public LogOutCommand(SelectionKey selectionKey) {
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Log out cannot happen before logging in!");
            }
            selectionKey.attach(null);
            return SUCCESSFUL_MESSAGE;
        } catch (NotLoggedInException e) {
            throw new UnsuccessfulCommandException("Logout command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
