package net.uber.entitytoitem;

public class ItemInfoExtractor {

    public static String extractOraxen(String oraxenInput) {
        return extractBetweenBraces(oraxenInput);
    }

    public static int extractCMD(String CMDInput) {
        return Integer.parseInt(extractBetweenBraces(CMDInput));
    }

    private static String extractBetweenBraces(String input) {
        int startIndex = input.indexOf('{') + 1;
        int endIndex = input.indexOf('}');
        if (startIndex > 0 && endIndex > startIndex) {
            return input.substring(startIndex, endIndex);
        } else {
            return "";
        }
    }

    public static boolean isOraxen(String input) {
        return input.startsWith("ORAXEN{") && input.endsWith("}");
    }

    public static boolean isCMD(String input) {
        return input.startsWith("CMD{") && input.endsWith("}");
    }

}

