package user;

import cryptowallet.CryptoWallet;
import exceptions.user.WrongPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.HashingAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserRepositoryTest {

    private UserRepository userRepository;
    private User user;

    private Map<String, User> getUsers() {
        user = new User(new AuthenticationData("username", "password"), new CryptoWallet());
        return Map.of("username", user);
    }

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(getUsers(), "repositoryTest.dat");
    }

    @Test
    void testSaveUserNullUser() {
        assertThrows(IllegalArgumentException.class, () -> userRepository.saveUser(null),
            "An IllegalArgumentException is expected when user is null reference!");
    }

    @Test
    void testSaveUser() {
        Map<String, User> mockedUsers = Mockito.mock();
        userRepository = new UserRepository(mockedUsers, "");
        userRepository.saveUser(user);

        String hashedPassword = HashingAlgorithm.hashPassword("password");

        verify(mockedUsers, times(1)).put("username", user);
        assertEquals(hashedPassword, user.authenticationData().getPassword(),
            "saveUser() does not hash the password!");
    }

    @Test
    void testUserExistsNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> userRepository.userExists(null),
            "An IllegalArgumentException is expected when username is null reference!");
    }

    @Test
    void testUserExistsTrue() {
        assertTrue(userRepository.userExists("username"), "User must be found in the repository!");
    }

    @Test
    void testUserExistsFalse() {
        assertFalse(userRepository.userExists("fakeUsername"), "User should not be found in the repository!");
    }

    @Test
    void testGetUserByAuthenticationDataNull() {
        assertThrows(IllegalArgumentException.class, () -> userRepository.getUserByAuthenticationData(null),
            "An IllegalArgumentException is expected when authenticationData is null reference!");
    }

    @Test
    void testGetUserByAuthenticationDataWrongPassword() {
        userRepository = new UserRepository(getUsers(), "");

        AuthenticationData authenticationData = new AuthenticationData("username", "wrongPassword");
        assertThrows(WrongPasswordException.class,
            () -> userRepository.getUserByAuthenticationData(authenticationData),
            "A WrongPasswordException is expected!");
    }

    @Test
    void testGetUserByAuthenticationData() throws WrongPasswordException {
        Map<String, User> users = getUsers();
        users.get("username").authenticationData().setPassword(HashingAlgorithm.hashPassword("password"));

        userRepository = new UserRepository(users, "");

        AuthenticationData authenticationData = new AuthenticationData("username", "password");
        User result = userRepository.getUserByAuthenticationData(authenticationData);
        assertEquals(user, result, "getUserByAuthenticationData() does not return the correct User instance!");
    }

    @Test
    public void testConstructor() throws IOException {
        setUpFile();

        Map<String, User> users = userRepository.getUsers();

        assertTrue(users.containsKey("username1"));
        assertTrue(users.containsKey("username2"));
        assertFalse(users.containsKey("username3"));

        tearDown();
    }

    @Test
    public void testClose() throws IOException, ClassNotFoundException {
        userRepository.close();

        Map<String, User> loadedUsers;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("repositoryTest.dat"))) {
            loadedUsers = (Map<String, User>) objectInputStream.readObject();
        }

        assertTrue(loadedUsers.containsKey("username"));
        assertFalse(loadedUsers.containsKey("fakeUsername"));

        tearDown();
    }

    private void setUpFile() throws IOException {
        Map<String, User> testUsers = Map.of(
            "username1", new User(new AuthenticationData("username1", "password1"), new CryptoWallet()),
            "username2", new User(new AuthenticationData("username2", "password2"), new CryptoWallet())
        );

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
            new FileOutputStream("repositoryTest.dat"))) {
            objectOutputStream.writeObject(testUsers);
        }

        userRepository = new UserRepository("repositoryTest.dat");
    }

    private void tearDown() {
        File file = new File("repositoryTest.dat");
        file.delete();
    }
}
