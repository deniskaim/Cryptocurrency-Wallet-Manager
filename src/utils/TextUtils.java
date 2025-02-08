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

    public static String getTheRestOfTheString(String input, String start) {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null reference");
        }
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null reference");
        }

        int startIndex = input.indexOf(start);
        if (startIndex == -1) {
            return null;
        }

        return input.substring(startIndex + start.length());
    }
}
