package server;

import coinapi.client.CoinApiClient;
import command.CommandExecutor;
import command.CommandFactory;
import command.pattern.Command;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import cryptowallet.CryptoWalletService;
import user.UserAccountService;
import user.UserRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CryptoWalletServer {

    public final int serverPort;
    private static final String SERVER_HOST = "localhost";
    private static final String APIKEY = "146865f1-12e8-4f3e-b75d-6d793420e4ae";

    private static final int BUFFER_SIZE = 2048;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private CommandFactory commandFactory;
    private final CommandExecutor commandExecutor;

    private Selector selector;
    private boolean isRunning = true;

    public CryptoWalletServer(int port, CommandExecutor commandExecutor) {
        this.serverPort = port;
        this.commandExecutor = commandExecutor;
    }

    public static void main(String[] args) {
        final int port = 8888;
        CryptoWalletServer cryptoWalletServer = new CryptoWalletServer(port, new CommandExecutor());
        cryptoWalletServer.start();
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             UserRepository userRepository = new UserRepository();
             CoinApiClient coinApiClient =
                 new CoinApiClient(HttpClient.newHttpClient(), APIKEY);
             ExecutorService executor = Executors.newSingleThreadExecutor()) {

            selector = Selector.open();
            configureServerSocket(serverSocketChannel);
            commandFactory = createCommandFactory(userRepository, coinApiClient);
            Runnable listenForStopCommandRunnable = new StopServerRunnable(this);
            executor.submit(listenForStopCommandRunnable);
            while (isRunning) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    iterateSelectedKeys(selectedKeys);
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                } catch (RuntimeException e) {
                    System.out.println("Client made an invalid request!");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("A problem with the server socket has arisen", e);
        }
    }

    public void stop() {
        isRunning = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocket(ServerSocketChannel serverSocketChannel)
        throws IOException {
        if (serverSocketChannel == null) {
            throw new IllegalArgumentException("serverSocketChannel cannot be null reference!");
        }
        serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, serverPort));
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
        } catch (InvalidCommandException | IncorrectArgumentsCountException | UnsuccessfulCommandException e) {
            String exceptionMessage = e.getMessage();
            writeToClient(exceptionMessage, (SocketChannel) selectionKey.channel());
        }
    }

    private CommandFactory createCommandFactory(UserRepository userRepository, CoinApiClient coinApiClient) {
        UserAccountService userAccountService = new UserAccountService(userRepository);
        CryptoWalletService cryptoWalletService = new CryptoWalletService(coinApiClient);

        return CommandFactory.getInstance(userAccountService, cryptoWalletService);
    }
}
