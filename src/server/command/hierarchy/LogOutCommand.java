package server.command.hierarchy;

import server.system.User;
import server.system.UserSystem;

import java.nio.channels.SelectionKey;

public class LogOutCommand implements Command {

    private final UserSystem userSystem;
    private final SelectionKey selectionKey;

    public LogOutCommand(String[] args, UserSystem userSystem, SelectionKey selectionKey) {
        if (args == null || args.length != 0) {
            throw new IllegalArgumentException("LogOut command should not include extra words!");
        }
        if (userSystem == null) {
            throw new IllegalArgumentException("userSystem cannot be null!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }
        this.userSystem = userSystem;
        this.selectionKey = selectionKey;
    }

    @Override
    public void execute() {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new IllegalStateException("Log out cannot happen before logging in!");
        }
        selectionKey.attach(null);
    }
}
