package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextUtilsTest {

    @Test
    void testGetSubstringsFromStringNullInput() {
        assertThrows(IllegalArgumentException.class, () ->
                TextUtils.getSubstringsFromString(null),
            "An IllegalArgumentException is expected when input is null reference!");
    }

    @Test
    void testGetSubstringsFromStringValidInput() {
        String input = "This is a test input";
        String[] substrings = TextUtils.getSubstringsFromString(input);

        final int expectedLength = 5;
        assertEquals(expectedLength, substrings.length);
        assertArrayEquals(new String[] {"This", "is", "a", "test", "input"}, substrings,
            "getSubstrings does not return the correct array of substrings!");
    }

    @Test
    void testGetTheRestOfTheStringNullInput() {

        assertThrows(IllegalArgumentException.class, () -> TextUtils.getTheRestOfTheString(null, "start"),
            "an IllegalArgumentException is expected when input is null reference!");
    }

    @Test
    void testGetTheRestOfTheStringNullStart() {
        assertThrows(IllegalArgumentException.class, () -> TextUtils.getTheRestOfTheString("string", null),
            "an IllegalArgumentException is expected when start is null reference!");

    }

    @Test
    void testGetTheRestOfTheStringCorrectStart() {
        String input = "This is a test string";
        String start = "This";

        String result = TextUtils.getTheRestOfTheString(input, start);

        assertEquals(" is a test string", result, "getTheRestOfTheString() does not work correctly!");
    }

    @Test
    void testGetTheRestOfTheStringIncorrectStart() {
        String input = "This is a test string";
        String start = "wrongStart";

        assertThrows(IllegalArgumentException.class, () ->
                TextUtils.getTheRestOfTheString(input, start),
            "An IllegalArgumentException is expected when start is actually not in the beginning of input!");
    }
}
