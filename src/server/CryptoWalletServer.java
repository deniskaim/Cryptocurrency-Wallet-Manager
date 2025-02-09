package server;

import coinapi.client.CoinApiClient;
import command.CommandExecutor;
import command.CommandFactory;
import command.pattern.Command;
import cryptowallet.AssetStorage;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import cryptowallet.CryptoWalletService;
import logs.FileLogger;
import user.User;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class CryptoWalletServer {

    public final int serverPort;
    private static final String SERVER_HOST = "localhost";
    private static final String APIKEY = "146865f1-12e8-4f3e-b75d-6d793420e4ae";
    private static final String REPOSITORY_FILE_NAME = "CryptoWalletUsersData.txt";

    private static final int BUFFER_SIZE = 2048;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private CommandFactory commandFactory;
    private final CommandExecutor commandExecutor = new CommandExecutor();
    private final CoinApiClient coinApiClient = new CoinApiClient(HttpClient.newHttpClient(), APIKEY);

    private Selector selector;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public CryptoWalletServer(int port) {
        this.serverPort = port;
    }

    public static void main(String[] args) {
        final int port = 8888;
        CryptoWalletServer cryptoWalletServer = new CryptoWalletServer(port);
        cryptoWalletServer.start();
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             UserRepository userRepository = new UserRepository(REPOSITORY_FILE_NAME);
             AssetStorage assetStorage = new AssetStorage(coinApiClient);
             ExecutorService executor = Executors.newSingleThreadExecutor()) {

            selector = Selector.open();
            configureServerSocket(serverSocketChannel);
            configureServerStopThread(executor);
            commandFactory = createCommandFactory(userRepository, assetStorage);
            while (isRunning.get()) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    iterateSelectedKeys(selectedKeys);
                } catch (IOException e) {
                    FileLogger.logError("An IOException occurred while processing a client's request!", e);
                }
            }
        } catch (IOException e) {
            FileLogger.logError("An IOException occurred while trying to start the CryptoWalletServer!", e);
        } catch (Exception e) {
            FileLogger.logError("An unexpected error occurred while the server was running!", e);
        }
    }

    public void stop() {
        isRunning.set(false);
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocket(ServerSocketChannel serverSocketChannel) throws IOException {
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
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();

        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void handleReadableSelectionKey(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        String clientMessage = readFromClient(socketChannel);
        if (clientMessage == null) {
            return;
        }
        executeCommand(clientMessage, selectionKey);
    }

    private String readFromClient(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        int resultRead = socketChannel.read(buffer);
        if (resultRead < 0) {
            socketChannel.close();
            return null;
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
        if (socketChannel.isOpen()) {
            socketChannel.write(buffer);
        }
    }

    private void executeCommand(String clientMessage, SelectionKey selectionKey) throws IOException {
        try {
            Command clientCommand = commandFactory.createCommand(clientMessage, selectionKey);
            String successfulMessage = commandExecutor.executeCommand(clientCommand);
            writeToClient(successfulMessage, (SocketChannel) selectionKey.channel());
        } catch (InvalidCommandException | IncorrectArgumentsCountException | UnsuccessfulCommandException e) {
            String exceptionMessage = e.getMessage();
            writeToClient(exceptionMessage, (SocketChannel) selectionKey.channel());
        } catch (IllegalArgumentException e) {
            if (selectionKey.attachment() != null) {
                User user = (User) selectionKey.attachment();
                String username = user.authenticationData().getUsername();
                FileLogger.logError("An unexpected error occurred while handling the request of user: " + username, e);
            }
        }
    }

    private CommandFactory createCommandFactory(UserRepository userRepository, AssetStorage assetStorage) {
        UserAccountService userAccountService = new UserAccountService(userRepository);
        CryptoWalletService cryptoWalletService = new CryptoWalletService(assetStorage);

        return CommandFactory.getInstance(userAccountService, cryptoWalletService);
    }

    private void configureServerStopThread(ExecutorService executor) {
        Runnable listenForStopCommandRunnable = new StopServerRunnable(this);
        executor.submit(listenForStopCommandRunnable);
    }
}
