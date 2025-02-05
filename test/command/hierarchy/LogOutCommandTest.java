package command.hierarchy;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.account.UserAccountService;
import user.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogOutCommandTest {

    private String[] args;
    private SelectionKey selectionKey;
    private User user;

    private LogOutCommand command;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException, InvalidCommandException {
        args = new String[0];
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);

        command = new LogOutCommand(args, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new LogOutCommand(null, selectionKey),
            "An IllegalArgumentException is expected when args in LogOutCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new LogOutCommand(args, null),
            "An IllegalArgumentException is expected when selectionKey in LogOutCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new LogOutCommand(new String[] {"oneString"}, selectionKey),
            "An IncorrectArgumentsCountException is expected when LogOutCommand contains one argument!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void execute() throws UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(user);

        String result = command.execute();
        assertEquals("You have successfully logged out!", result);
        verify(selectionKey, times(1)).attach(null);
    }
}
