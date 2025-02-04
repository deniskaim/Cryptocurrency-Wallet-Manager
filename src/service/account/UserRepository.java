package service.account;

import exceptions.WrongPasswordException;
import user.AuthenticationData;
import user.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static utils.HashingAlgorithm.hashPassword;

public class UserRepository implements AutoCloseable {
    private static final String FILE_NAME = "CryptoWalletUsersData.txt";
    private final Map<String, User> mapUsernameAccount;

    public UserRepository() {
        mapUsernameAccount = loadSavedRegisteredUsers();
    }

    public void saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null reference!");
        }
        setHashedPassword(user);
        mapUsernameAccount.put(user.authenticationData().getUsername(), user);
    }

    public boolean userExists(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null reference!");
        }
        return mapUsernameAccount.containsKey(username);
    }

    public User getUserByAuthenticationData(AuthenticationData authenticationData) throws WrongPasswordException {
        if (authenticationData == null) {
            throw new IllegalArgumentException("username cannot be null reference!");
        }

        String triedPassword = authenticationData.getPassword();
        String hashedTriedPassword = hashPassword(triedPassword);

        String actualPassword = getPasswordByUsername(authenticationData.getUsername());
        if (!actualPassword.equals(hashedTriedPassword)) {
            throw new WrongPasswordException("The password is incorrect!");
        }

        return mapUsernameAccount.get(authenticationData.getUsername());
    }

    @Override
    public void close() {
        saveUsersToFile();
    }

    private Map<String, User> loadSavedRegisteredUsers() {
        Map<String, User> usersFromFile = new HashMap<>();

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            usersFromFile = (Map<String, User>) reader.readObject();
        } catch (FileNotFoundException e) {
            return usersFromFile;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("A problem occurred while loading the registered users!", e);
        }
        return usersFromFile;
    }

    private void saveUsersToFile() {
        try {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                objectOutputStream.writeObject(mapUsernameAccount);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not save the users to the file!", e);
        }
    }

    private String getPasswordByUsername(String username) {
        return mapUsernameAccount.get(username).authenticationData().getPassword();
    }

    private void setHashedPassword(User user) {
        String hashedPassword = hashPassword(user.authenticationData().getPassword());
        user.authenticationData().setPassword(hashedPassword);
    }
}
