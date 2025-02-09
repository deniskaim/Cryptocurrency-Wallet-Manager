package logs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {

    private static final Logger LOGGER = Logger.getLogger(FileLogger.class.getName());

    private static final String PATH = "logs";
    private static final String FILE_NAME = "cryptoWalletApp.log";
    private static final String LOG_FILE = PATH + File.separator + FILE_NAME;

    static {
        try {
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.ALL);

            checkForDirectory();

            FileHandler fileHandler = configureFileHandler();
            ConsoleHandler consoleHandler = configureConsoleHandler();

            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(consoleHandler);
        } catch (IOException e) {
            System.err.println("Unsuccessful try to create a logger for the cryptoWalletApp: " + e.getMessage());
        }
    }

    public static void logError(String message, Throwable cause) {
        LOGGER.log(Level.ALL, message, cause);
    }

    private static FileHandler configureFileHandler() throws IOException {
        FileHandler customFileHandler = new FileHandler(LOG_FILE, true);
        customFileHandler.setLevel(Level.ALL);
        customFileHandler.setFormatter(new SimpleFormatter());

        return customFileHandler;
    }

    private static ConsoleHandler configureConsoleHandler() {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new SimpleFormatter());
        return consoleHandler;
    }

    private static void checkForDirectory() throws IOException {
        Path logsPath = Path.of(PATH);
        if (Files.notExists(logsPath)) {
            Files.createDirectories(logsPath);
        }
    }
}
