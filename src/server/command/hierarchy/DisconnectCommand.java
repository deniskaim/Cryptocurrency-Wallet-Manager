package server.command.hierarchy;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class DisconnectCommand implements Command {

    private final SelectionKey selectionKey;

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
    public void execute() {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not close the socket", e);
        }
    }

}
