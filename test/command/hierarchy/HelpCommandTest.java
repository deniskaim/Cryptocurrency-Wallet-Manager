package command.hierarchy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class HelpCommandTest {

    private final HelpCommand helpCommand = new HelpCommand();

    @Test
    void testExecute() {
        String result = helpCommand.execute();
        assertFalse(result.isEmpty(), "HelpCommand should not contain an empty message!");
    }
}
