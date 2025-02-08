package command.pattern;

import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.UsernameAlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.UserAccountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RegisterCommandTest {

    private String username;
    private String password;
    private UserAccountService userAccountService;

    private RegisterCommand command;

    @BeforeEach
    void setUp() {
        username = "username";
        password = "password";
        userAccountService = Mockito.mock(UserAccountService.class);

        command = new RegisterCommand(username, password, userAccountService);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RegisterCommand(null, password, userAccountService),
            "An IllegalArgumentException is expected when username in RegisterCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RegisterCommand(username, null, userAccountService),
            "An IllegalArgumentException is expected when password in RegisterCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenUserAccountServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RegisterCommand(username, password, null),
            "An IllegalArgumentException is expected when userAccountService in RegisterCommand is null reference!");
    }

    @Test
    void testExecuteWhenUsernameIsTaken() throws UsernameAlreadyTakenException {
        doThrow(UsernameAlreadyTakenException.class).when(userAccountService).registerUser("username", "password");

        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the username is taken.");
    }

    @Test
    void testExecute() throws UnsuccessfulCommandException, UsernameAlreadyTakenException {
        String expectedResult = "You have successfully registered in the system";
        doNothing().when(userAccountService).registerUser("username", "password");
        String result = command.execute();

        assertEquals(expectedResult, result, "RegisterCommand doesn't return the correct string!");
        verify(userAccountService, times(1)).registerUser("username", "password");
    }
}
