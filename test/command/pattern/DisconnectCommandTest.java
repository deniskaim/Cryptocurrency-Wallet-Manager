package command.pattern;

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
    private SelectionKey selectionKey;
    private SocketChannel socketChannel;

    @BeforeEach
    void setUp() {
        selectionKey = Mockito.mock(SelectionKey.class);
        socketChannel = Mockito.mock(SocketChannel.class);
        when(selectionKey.channel()).thenReturn(socketChannel);

        disconnectCommand = new DisconnectCommand(selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DisconnectCommand(null),
            "An IllegalArgumentException is expected when selectionKey in DisconnectCommand is null reference!");
    }

    @Test
    void testExecuteShouldThrowUnsuccessfulCommandExceptionWhenIOException()
        throws IOException {
        doThrow(IOException.class).when(socketChannel).close();

        assertThrows(UnsuccessfulCommandException.class, () -> disconnectCommand.execute(),
            "An UnsuccessfulCommandException is expected when an IOException occurs");
    }

    @Test
    void testExecute() throws IOException, UnsuccessfulCommandException {
        doNothing().when(socketChannel).close();
        String result = disconnectCommand.execute();

        assertEquals("You have been disconnected from the server!", result);
        verify(socketChannel, times(1)).close();
        verify(selectionKey, times(1)).cancel();
    }
}
