package pl.dcrft.Utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    public static String translateHexColorCodes(final String message) {
        final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher matcher = hexPattern.matcher(message);
        final StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

    public static String reformatRGB(String message) {

        // Translate RGB codes
        message = message.replaceAll("(?i)\\&(x|#)([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])", "&x&$2&$3&$4&$5&$6&$7");

        StringBuilder transformedMessage = new StringBuilder();
        char lastChar = 'a';

        // Transform codes to lowercase for better compatibility with Essentials etc.
        for (char c : message.toCharArray()) {

            if (lastChar == '&') {
                if (String.valueOf(c).matches("(?i)([0-9A-FX])")) {
                    c = Character.toLowerCase(c);
                }
            }

            transformedMessage.append(c);
            lastChar = c;
        }

        return transformedMessage.toString();

    }
    public static String colorize (String toColorize){
        return ChatColor.translateAlternateColorCodes('&', ColorUtil.reformatRGB(toColorize));
    }

}
