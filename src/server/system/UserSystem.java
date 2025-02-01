package server.system;

import exceptions.UserNotFoundException;
import exceptions.UsernameAlreadyTakenException;
import exceptions.WrongPasswordException;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserSystem {
    private static final String FILE_NAME = "CryptoWalletUsersData.txt";
    private final Map<String, User> mapUsernameAccount = new HashMap<>();

    public UserSystem() {
        Set<User> registeredUsers = loadSavedRegisteredUsers();
        for (User user : registeredUsers) {
            mapUsernameAccount.put(user.username(), user);
        }
    }

    public void registerUser(String username, String password) throws UsernameAlreadyTakenException {
        validateUsername(username);
        validatePassword(password);

        if (mapUsernameAccount.containsKey(username)) {
            throw new UsernameAlreadyTakenException(
                "There is already an account with this username. Try with another one!");
        }
        CryptoWallet wallet = new CryptoWallet();
        User newUser = new User(username, password, wallet);

        saveUser(newUser);
        mapUsernameAccount.put(username, newUser);
    }

    public User logInUser(String username, String password) throws UserNotFoundException, WrongPasswordException {
        validateUsername(username);
        validatePassword(password);

        if (!mapUsernameAccount.containsKey(username)) {
            throw new UserNotFoundException("There is no such registered user in the system!");
        }

        User user = mapUsernameAccount.get(username);

        String actualPassword = user.password();
        if (!actualPassword.equals(password)) {
            throw new WrongPasswordException("The password is incorrect!");
        }

        return user;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Invalid Username for registration!");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Invalid Password for registration!");
        }
    }

    private Set<User> loadSavedRegisteredUsers() {

        Set<User> usersFromFile = new HashSet<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            while (true) {
                try {
                    User currentUser = (User) reader.readObject();
                    usersFromFile.add(currentUser);
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

    private void saveUser(User newUser) {
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(FILE_NAME, true))) {
            writer.writeObject(newUser);
        } catch (IOException e) {
            throw new RuntimeException("Could not save the new user with the already registered users!", e);
        }
    }
}
