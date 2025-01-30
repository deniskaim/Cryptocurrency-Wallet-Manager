package server;

import java.nio.ByteBuffer;

public class CryptoWalletServer {

    public static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
}
