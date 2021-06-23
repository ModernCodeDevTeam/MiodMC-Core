package pl.dcrft.Utils;

import org.bukkit.entity.Player;

import java.util.Collection;

public class GroupUtil {

    public static boolean isPlayerInGroup(final Player player, final String group) {
        return player.hasPermission("group." + group);
    }
    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }
}
