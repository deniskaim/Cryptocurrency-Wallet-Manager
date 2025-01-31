package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class CryptoWalletServer {

    public static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private static void configureServerSocket(ServerSocketChannel serverSocketChannel, Selector selector)
        throws IOException {
        if (serverSocketChannel == null) {
            throw new IllegalArgumentException("serverSocketChannel cannot be null reference!");
        }
        if (selector == null) {
            throw new IllegalArgumentException("selector cannot be null reference!");
        }

        serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    // incoming connections requests from clients
    private static void handleAcceptableSelectionKey(SelectionKey selectionKey, Selector selector) throws IOException {
        if (selectionKey == null || !selectionKey.isAcceptable()) {
            throw new IllegalArgumentException("The selectionKey is invalid!");
        }
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();

        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    // incoming requests in form of "reading" the client's message
    private void handleReadableSelectionKey(SelectionKey selectionKey) throws IOException {
        if (selectionKey == null || !selectionKey.isReadable()) {
            throw new IllegalArgumentException("The selectionKey is invalid!");
        }
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();
        String clientMessage = new String(buffer.array(), 0, buffer.position(), "UTF-8");

    }

    public static void main(String[] args) {
        CryptoWalletServer cryptoWalletServer = new CryptoWalletServer();

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            configureServerSocket(serverSocketChannel, selector);
            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isReadable()) {
                        cryptoWalletServer.handleReadableSelectionKey(selectionKey);
                    } else if (selectionKey.isAcceptable()) {
                        handleAcceptableSelectionKey(selectionKey, selector);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("A problem with the server socket has arisen", e);
        }
    }
}
