package utils;

public class TextUtils {
    private static final String WORD_SEPARATOR = "\\s+";

    public static String[] getSubstringsFromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null reference");
        }
        input = input.trim();
        return input.split(WORD_SEPARATOR);
    }
}
