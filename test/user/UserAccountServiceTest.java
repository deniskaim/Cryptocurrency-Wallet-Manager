package user;

import exceptions.user.UserNotFoundException;
import exceptions.user.UsernameAlreadyTakenException;
import exceptions.user.WrongPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserAccountServiceTest {

    private UserRepository mockUserRepository;
    private UserAccountService userAccountService;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @BeforeEach
    void setUp() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        userAccountService = new UserAccountService(mockUserRepository);
    }

    @Test
    void testRegisterUserNullUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.registerUser(null, PASSWORD),
            "An IllegalArgumentException is expected when username is null reference!");
    }

    @Test
    void testRegisterUserEmptyUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.registerUser("", PASSWORD),
            "An IllegalArgumentException is expected when the username is empty!");
    }

    @Test
    void testRegisterUserBlankUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.registerUser("  ", PASSWORD),
            "An IllegalArgumentException is expected when the username is blank!");
    }

    @Test
    void testRegisterUserNullPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.registerUser(USERNAME, null),
            "An IllegalArgumentException is expected when password is null reference!");
    }

    @Test
    void testRegisterUserEmptyPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.registerUser(USERNAME, ""),
            "An IllegalArgumentException is expected when the password is empty!");
    }

    @Test
    void testRegisterUserBlankPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.registerUser(USERNAME, "  "),
            "An IllegalArgumentException is expected when the password is blank!");
    }

    @Test
    void testRegisterUserWhenUsernameIsTaken() {
        when(mockUserRepository.userExists(USERNAME)).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class, () -> userAccountService.registerUser(USERNAME, PASSWORD),
            "An UsernameAlreadyTakenException is expected when the username is already taken!");
    }

    @Test
    void testRegisterUser() throws UsernameAlreadyTakenException {
        when(mockUserRepository.userExists(USERNAME)).thenReturn(false);
        userAccountService.registerUser(USERNAME, PASSWORD);

        verify(mockUserRepository, times(1)).saveUser(any(User.class));
    }

    @Test
    void testLogInUserNullUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.logInUser(null, PASSWORD),
            "An IllegalArgumentException is expected when username is null reference!");
    }

    @Test
    void testLogInUserEmptyUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.logInUser("", PASSWORD),
            "An IllegalArgumentException is expected when the username is empty!");
    }

    @Test
    void testLogInUserBlankUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.logInUser("  ", PASSWORD),
            "An IllegalArgumentException is expected when the username is blank!");
    }

    @Test
    void testLogInUserNullPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.logInUser(USERNAME, null),
            "An IllegalArgumentException is expected when password is null reference!");
    }

    @Test
    void testLogInUserEmptyPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.logInUser(USERNAME, ""),
            "An IllegalArgumentException is expected when the password is empty!");
    }

    @Test
    void testLogInUserBlankPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> userAccountService.logInUser(USERNAME, "  "),
            "An IllegalArgumentException is expected when the password is blank!");
    }

    @Test
    void testLogInUserWhenUserIsNotFound() {
        when(mockUserRepository.userExists(USERNAME)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userAccountService.logInUser(USERNAME, PASSWORD),
            "An UserNotFoundException is expected when such an user is not registered!");
    }

    @Test
    void testLogInUserWrongPassword() throws WrongPasswordException {
        when(mockUserRepository.userExists(USERNAME)).thenReturn(true);
        when(mockUserRepository.getUserByAuthenticationData(any(AuthenticationData.class)))
            .thenThrow(WrongPasswordException.class);

        assertThrows(WrongPasswordException.class,
            () -> userAccountService.logInUser(USERNAME, "wrongPassword"),
            "An WrongPasswordException is expected when the password is found to be incorrect!");
    }

    @Test
    void testLogIn() throws WrongPasswordException, UserNotFoundException {
        User mockUser = Mockito.mock(User.class);

        when(mockUserRepository.userExists(USERNAME)).thenReturn(true);
        when(mockUserRepository.getUserByAuthenticationData(any(AuthenticationData.class))).thenReturn(mockUser);

        User resultUser = userAccountService.logInUser(USERNAME, PASSWORD);
        assertEquals(mockUser, resultUser, "userAccountService does not return the correct instance!");
    }

}
