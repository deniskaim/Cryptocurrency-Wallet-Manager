package command.pattern;

import exceptions.command.IncorrectArgumentsCountException;
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

    private String[] args;
    private UserAccountService userAccountService;

    private RegisterCommand command;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException {
        args = new String[] {"username", "password"};
        userAccountService = Mockito.mock(UserAccountService.class);

        command = new RegisterCommand(args, userAccountService);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RegisterCommand(null, userAccountService),
            "An IllegalArgumentException is expected when args in RegisterCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenUserAccountServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RegisterCommand(args, null),
            "An IllegalArgumentException is expected when userAccountService in RegisterCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new RegisterCommand(new String[] {"onlyOneString"}, userAccountService),
            "An IncorrectArgumentsCountException is expected when RegisterCommand contains one argument!");
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
