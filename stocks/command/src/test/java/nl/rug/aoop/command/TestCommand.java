package nl.rug.aoop.command;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test class for the Command Interface.
 */
public class TestCommand {

    /**
     * Test the execution of a command with a mock.
     */
    @Test
    public void testCommandExecutionWithMock() {
        Command mockCommand = mock(Command.class);

        Map<String, Object> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("key2", "value2");

        mockCommand.execute(params);

        verify(mockCommand).execute(params);
    }
}
