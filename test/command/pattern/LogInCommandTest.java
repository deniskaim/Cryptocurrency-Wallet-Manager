package command.pattern;

import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.UserNotFoundException;
import exceptions.user.WrongPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.UserAccountService;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogInCommandTest {

    private String username;
    private String password;
    private UserAccountService userAccountService;
    private SelectionKey selectionKey;
    private User user;

    private LogInCommand command;

    @BeforeEach
    void setUp() {
        username = "username";
        password = "password";
        userAccountService = Mockito.mock(UserAccountService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);

        command = new LogInCommand(username, password, userAccountService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new LogInCommand(null, password, userAccountService, selectionKey),
            "An IllegalArgumentException is expected when username in LogInCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new LogInCommand(username, null, userAccountService, selectionKey),
            "An IllegalArgumentException is expected when password in LogInCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenUserAccountServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new LogInCommand(username, password, null, selectionKey),
            "An IllegalArgumentException is expected when userAccountService in LogInCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new LogInCommand(username, password, userAccountService, null),
            "An IllegalArgumentException is expected when selectionKey in LogInCommand is null reference!");
    }

    @Test
    void testExecuteWhenUserIsAlreadyLoggedIn() {
        when(selectionKey.attachment()).thenReturn(user);
        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the user is already logged in!");
    }

    @Test
    void testExecuteWhenPasswordIsWrong() throws UserNotFoundException, WrongPasswordException {
        when(selectionKey.attachment()).thenReturn(null);
        when(userAccountService.logInUser("username", "password")).thenThrow(WrongPasswordException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the password is incorrect!");
    }

    @Test
    void testExecuteWhenUserIsNotRegistered() throws UserNotFoundException, WrongPasswordException {
        when(selectionKey.attachment()).thenReturn(null);
        when(userAccountService.logInUser("username", "password")).thenThrow(UserNotFoundException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the user is not registered!");
    }

    @Test
    void testExecute() throws UserNotFoundException, WrongPasswordException, UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(null);
        when(userAccountService.logInUser("username", "password")).thenReturn(user);

        String expectedResult = "You have successfully logged in as \"username\"";
        String result = command.execute();

        assertEquals(expectedResult, result, "execute() does not return the correct string!");
        verify(selectionKey, times(1)).attach(user);
    }
}
