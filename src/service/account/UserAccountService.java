package service.account;

import exceptions.UserNotFoundException;
import exceptions.UsernameAlreadyTakenException;
import exceptions.WrongPasswordException;
import user.AuthenticationData;
import user.CryptoWallet;
import user.User;

public class UserAccountService {

    private final UserRepository userRepository;

    public UserAccountService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("userRepository cannot be null reference!");
        }
        this.userRepository = userRepository;
    }

    public void registerUser(String username, String password) throws UsernameAlreadyTakenException {
        validateUsername(username);
        validatePassword(password);

        if (userRepository.userExists(username)) {
            throw new UsernameAlreadyTakenException(
                "There is already an account with this username. Try with another one!");
        }

        CryptoWallet wallet = new CryptoWallet();
        User newUser = User.of(AuthenticationData.of(username, password), wallet);

        userRepository.saveUser(newUser);
    }

    public User logInUser(String username, String password) throws UserNotFoundException, WrongPasswordException {
        validateUsername(username);
        validatePassword(password);

        if (!userRepository.userExists(username)) {
            throw new UserNotFoundException("There is no such registered user in the system!");
        }

        return userRepository.getUserByAuthenticationData(AuthenticationData.of(username, password));
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
}
