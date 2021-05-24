package pl.dcrft.Utils;

import org.bukkit.entity.Player;

public class GroupUtil {

    public static boolean isPlayerInGroup(final Player player, final String group) {
        return player.hasPermission("group." + group);
    }
}
