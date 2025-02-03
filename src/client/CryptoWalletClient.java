package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CryptoWalletClient {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFFER_SIZE = 2048;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private static final String WELCOME_MESSAGE =
        "Welcome to the Cryptocurrency Wallet Manager. Use the command \"help\" to see the functionalities!";

    public static void main(String[] args) {

        CryptoWalletClient client = new CryptoWalletClient();

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println(WELCOME_MESSAGE);
            while (true) {
                System.out.println("Enter a command: ");
                String clientMessage = scanner.nextLine();

                try {
                    client.writeToServer(socketChannel, clientMessage);
                    String replyFromServer = client.readFromServer(socketChannel);
                    System.out.println(replyFromServer);
                    System.out.println();
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                    break;
                } catch (RuntimeException e) {
                    System.out.println("Invalid command!");
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private void writeToServer(SocketChannel socketChannel, String message) throws IOException {
        if (socketChannel == null) {
            throw new IllegalArgumentException("The socketChannel of the client is null reference!");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("The message the client wants to send is invalid!");
        }

        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private String readFromServer(SocketChannel socketChannel) throws IOException {
        if (socketChannel == null) {
            throw new IllegalArgumentException("The socketChannel of the client is null reference!");
        }

        buffer.clear();
        int resultRead = socketChannel.read(buffer);
        if (resultRead < 0) {
            throw new IllegalStateException("You have been disconnected from the server!");
        }

        buffer.flip();
        byte[] input = new byte[buffer.remaining()];
        buffer.get(input);

        return new String(input, StandardCharsets.UTF_8);
    }
}
