package command.hierarchy;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DisconnectCommandTest {

    private DisconnectCommand disconnectCommand;
    private String[] args;
    private SelectionKey selectionKey;
    private SocketChannel socketChannel;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException {
        args = new String[0];
        selectionKey = Mockito.mock(SelectionKey.class);
        socketChannel = Mockito.mock(SocketChannel.class);
        when(selectionKey.channel()).thenReturn(socketChannel);

        disconnectCommand = new DisconnectCommand(args, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DisconnectCommand(null, selectionKey),
            "args in DisconnectCommand cannot be null!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DisconnectCommand(args, null),
            "selectionKey in DisconnectCommand cannot be null!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new DisconnectCommand(new String[] {"invalidParam"}, selectionKey),
            "DisconnectCommand should not contain arguments!");
    }

    @Test
    void testExecuteShouldThrowUnsuccessfulCommandExceptionWhenIOException()
        throws IOException, UnsuccessfulCommandException {
        doThrow(IOException.class).when(socketChannel).close();

        assertThrows(UnsuccessfulCommandException.class, () -> disconnectCommand.execute(),
            "DisconnectCommand should throw an UnsuccessfulCommandException when an IOException occurs");
    }

    @Test
    void testExecute() throws IOException, UnsuccessfulCommandException {
        doNothing().when(socketChannel).close();
        String result = disconnectCommand.execute();

        assertEquals("You have been disconnected from the server!", result);
        verify(socketChannel, times(1)).close();
    }
}
