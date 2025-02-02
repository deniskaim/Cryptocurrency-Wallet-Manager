package server.command.hierarchy;

import exceptions.NotLoggedInException;
import server.system.user.User;

import java.nio.channels.SelectionKey;

public class LogOutCommand implements Command {

    private final SelectionKey selectionKey;
    private static final String SUCCESSFUL_MESSAGE = "You have successfully logged out";

    public LogOutCommand(String[] args, SelectionKey selectionKey) {
        if (args == null || args.length != 0) {
            throw new IllegalArgumentException("LogOut command should not include extra words!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws NotLoggedInException {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new NotLoggedInException("Log out cannot happen before logging in!");
        }
        selectionKey.attach(null);
        return SUCCESSFUL_MESSAGE;
    }
}
