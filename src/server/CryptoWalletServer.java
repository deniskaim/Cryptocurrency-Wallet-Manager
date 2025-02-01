package server;

import server.command.CommandExecutor;
import server.command.CommandFactory;
import server.command.hierarchy.Command;
import server.system.UserSystem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class CryptoWalletServer {

    public static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private final UserSystem userSystem = new UserSystem();
    private final CommandFactory commandFactory = CommandFactory.getInstance(userSystem);
    private final CommandExecutor commandExecutor = new CommandExecutor();
    private Selector selector;

    public static void main(String[] args) {
        CryptoWalletServer cryptoWalletServer = new CryptoWalletServer();
        cryptoWalletServer.start();
    }

    private void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocket(serverSocketChannel);
            while (true) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    iterateSelectedKeys(selectedKeys);
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("A problem with the server socket has arisen", e);
        }
    }

    private void configureServerSocket(ServerSocketChannel serverSocketChannel)
        throws IOException {
        if (serverSocketChannel == null) {
            throw new IllegalArgumentException("serverSocketChannel cannot be null reference!");
        }
        serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void iterateSelectedKeys(Set<SelectionKey> selectedKeys) throws IOException {
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
        while (keyIterator.hasNext()) {
            SelectionKey selectionKey = keyIterator.next();

            if (selectionKey.isReadable()) {
                handleReadableSelectionKey(selectionKey);
                if (!selectionKey.channel().isOpen()) {
                    continue;
                }
            } else if (selectionKey.isAcceptable()) {
                handleAcceptableSelectionKey(selectionKey);
            }
            keyIterator.remove();
        }
    }

    private void handleAcceptableSelectionKey(SelectionKey selectionKey) throws IOException {
        if (selectionKey == null || !selectionKey.isAcceptable()) {
            throw new IllegalArgumentException("The selectionKey is invalid!");
        }
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();

        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void handleReadableSelectionKey(SelectionKey selectionKey) throws IOException {
        if (selectionKey == null || !selectionKey.isReadable()) {
            throw new IllegalArgumentException("The selectionKey is invalid!");
        }
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        String clientMessage = readFromClient(socketChannel);
        executeCommand(clientMessage, selectionKey);
    }

    private String readFromClient(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        int resultRead = socketChannel.read(buffer);
        if (resultRead < 0) {
            socketChannel.close();
            throw new IllegalStateException("Read operation is unsuccessful!");
        }

        buffer.flip();
        byte[] input = new byte[buffer.remaining()];
        buffer.get(input);

        return new String(input, StandardCharsets.UTF_8);
    }

    private void writeToClient(String message, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private void executeCommand(String clientMessage, SelectionKey selectionKey) throws IOException {
        try {
            Command clientCommand = commandFactory.createCommand(clientMessage, selectionKey);
            String successfulMessage = commandExecutor.executeCommand(clientCommand);
            if (selectionKey.channel().isOpen()) {
                writeToClient(successfulMessage, (SocketChannel) selectionKey.channel());
            }
        } catch (RuntimeException e) {
            String exceptionMessage = e.getMessage();
            writeToClient(exceptionMessage, (SocketChannel) selectionKey.channel());
        }
    }
}
