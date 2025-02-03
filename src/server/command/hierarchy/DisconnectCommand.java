package server.command.hierarchy;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class DisconnectCommand implements Command {

    private final SelectionKey selectionKey;
    private static final String SUCCESSFUL_MESSAGE = "You have been disconnected from the server!";

    public DisconnectCommand(String[] args, SelectionKey selectionKey) {
        if (args == null || args.length != 0) {
            throw new IllegalArgumentException("LogOut command should not include extra words!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.close();
        return SUCCESSFUL_MESSAGE;
    }

}
