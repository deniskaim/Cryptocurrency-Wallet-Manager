package server.command.hierarchy;

import server.system.User;
import server.system.UserSystem;

import java.nio.channels.SelectionKey;

public class LogOutCommand implements Command {

    private final SelectionKey selectionKey;

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
    public void execute() {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new IllegalStateException("Log out cannot happen before logging in!");
        }
        selectionKey.attach(null);
    }
}
