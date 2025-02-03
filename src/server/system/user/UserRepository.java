package server.system.user;

import exceptions.WrongPasswordException;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final String FILE_NAME = "CryptoWalletUsersData.txt";
    private final Map<String, User> mapUsernameAccount;

    public UserRepository() {
        mapUsernameAccount = loadSavedRegisteredUsers();
    }

    public void saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null reference!");
        }
        mapUsernameAccount.put(user.authenticationData().username(), user);
        saveUserToFile(user);
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

        String triedPassword = authenticationData.password();
        String actualPassword = getPasswordByUsername(authenticationData.username());
        if (!actualPassword.equals(triedPassword)) {
            throw new WrongPasswordException("The password is incorrect!");
        }

        return mapUsernameAccount.get(authenticationData.username());
    }

    private Map<String, User> loadSavedRegisteredUsers() {

        Map<String, User> usersFromFile = new HashMap<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            while (true) {
                try {
                    User currentUser = (User) reader.readObject();
                    usersFromFile.put(currentUser.authenticationData().username(), currentUser);
                } catch (EOFException e) {
                    break;
                }
            }
            return usersFromFile;
        } catch (FileNotFoundException e) {
            return usersFromFile;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("A problem occurred while loading the registered users!", e);
        }
    }

    private void saveUserToFile(User newUser) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                    objectOutputStream.writeObject(newUser);
                }
            } else {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(FILE_NAME, true)) {
                    @Override
                    protected void writeStreamHeader() throws IOException {

                    }
                }) {
                    objectOutputStream.writeObject(newUser);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not save the new user with the already registered users!", e);
        }
    }

    private String getPasswordByUsername(String username) {
        return mapUsernameAccount.get(username).authenticationData().password();
    }
}
