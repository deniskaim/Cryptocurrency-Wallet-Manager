package server;

import java.util.Scanner;

public class StopServerRunnable implements Runnable {

    private final CryptoWalletServer server;
    private static final String STOP_SERVER_MESSAGE = "stop";

    public StopServerRunnable(CryptoWalletServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals(STOP_SERVER_MESSAGE)) {
                server.stop();
                break;
            }
        }
    }
}
