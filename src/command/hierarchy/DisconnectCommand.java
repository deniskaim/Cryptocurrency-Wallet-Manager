package command.hierarchy;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class DisconnectCommand implements Command {

    private final SelectionKey selectionKey;
    private static final String SUCCESSFUL_MESSAGE = "You have been disconnected from the server!";

    public DisconnectCommand(String[] args, SelectionKey selectionKey) throws IncorrectArgumentsCountException {
        if (args == null) {
            throw new IllegalArgumentException("args in Disconnect command cannot be null reference!");
        }
        if (args.length != 0) {
            throw new IncorrectArgumentsCountException(
                "Disconnect command should not contain arguments!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.close();
            return SUCCESSFUL_MESSAGE;
        } catch (IOException e) {
            throw new UnsuccessfulCommandException("Disconnect command is unsuccessful! " + e.getMessage(), e);
        }
    }

}
