package command.pattern;

import exceptions.command.UnsuccessfulCommandException;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class DisconnectCommand implements Command {

    private final SelectionKey selectionKey;
    private static final String SUCCESSFUL_MESSAGE = "You have been disconnected from the server!";

    public DisconnectCommand(SelectionKey selectionKey) {
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey in Disconnect command cannot be null reference!");
        }
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.close();
            selectionKey.cancel();
            return SUCCESSFUL_MESSAGE;
        } catch (IOException e) {
            throw new UnsuccessfulCommandException("Disconnect command is unsuccessful! " + e.getMessage(), e);
        }
    }

}
