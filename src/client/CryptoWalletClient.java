package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CryptoWalletClient {

    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

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
            throw new IllegalStateException("Connection closed by server.");
        }

        buffer.flip();
        byte[] input = new byte[buffer.remaining()];
        buffer.get(input);

        return new String(input, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {

        CryptoWalletClient client = new CryptoWalletClient();

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            while (true) {
                System.out.println("Enter the desired command: ");
                String clientMessage = scanner.nextLine();

                try {
                    client.writeToServer(socketChannel, clientMessage);
                    String replyFromServer = client.readFromServer(socketChannel);
                    System.out.println(replyFromServer);
                    System.out.println();
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    break;
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
