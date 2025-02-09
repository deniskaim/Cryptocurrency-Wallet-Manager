package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CryptoWalletClient {

    private final int serverPort;
    private final String serverHost;

    private static final int BUFFER_SIZE = 2048;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private static final String WELCOME_MESSAGE =
        "Welcome to the Cryptocurrency Wallet Manager. Use the command \"$ help\" to see the functionalities!";

    private static final String ENTER_COMMAND_MESSAGE = "Enter a command: ";
    private static final String INVALID_COMMAND_MESSAGE = "Invalid command! Try again!";
    private static final String DISCONNECTED_MESSAGE = "You have been disconnected from the server!";

    public CryptoWalletClient(int port, String host) {
        this.serverPort = port;
        this.serverHost = host;
    }

    public static void main(String[] args) {
        final int port = 2468;
        final String host = "localhost";

        CryptoWalletClient client = new CryptoWalletClient(port, host);
        client.start();
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
            System.out.println(WELCOME_MESSAGE);
            while (true) {
                System.out.println(ENTER_COMMAND_MESSAGE);
                String clientMessage = scanner.nextLine();

                try {
                    writeToServer(socketChannel, clientMessage);
                    String replyFromServer = readFromServer(socketChannel);
                    if (replyFromServer == null) {
                        System.out.println(DISCONNECTED_MESSAGE);
                        break;
                    }
                    System.out.println(replyFromServer);
                    System.out.println();
                } catch (IllegalArgumentException e) {
                    System.out.println(INVALID_COMMAND_MESSAGE);
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to the server. Try again later!");
        } catch (Exception e) {
            System.out.println("A problem with the connection to the server has occurred!");
        }
    }

    private void writeToServer(SocketChannel socketChannel, String message) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("The message the client wants to send is invalid!");
        }
        if (message.isBlank()) {
            message = System.lineSeparator();
        }

        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private String readFromServer(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        int resultRead = socketChannel.read(buffer);
        if (resultRead < 0) {
            return null;
        }

        buffer.flip();
        byte[] input = new byte[buffer.remaining()];
        buffer.get(input);

        return new String(input, StandardCharsets.UTF_8);
    }
}
