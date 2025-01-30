package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class CryptoWalletClient {

    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private static final String QUIT_MESSAGE = "quit";

    public static void main() {

        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, "UTF-8"));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            while (true) {
                System.out.println("Enter the desired command: ");
                String clientMessage = scanner.nextLine();

                if (clientMessage.equals(QUIT_MESSAGE)) {
                    break;
                }

                writer.println(clientMessage);
                String reply = reader.readLine();

                System.out.println(reply);
                System.out.println();
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
