package pl.dcrft.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;

import java.util.ArrayList;
import java.util.List;


public class TabCompleteListener implements Listener {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandSendEvent(PlayerCommandSendEvent e) {
        Player p = e.getPlayer();
        FileConfiguration disabledConfig = ConfigManager.getDisabledFile();
        if (disabledConfig.getKeys(false) != null) {
            List<String> blocked = new ArrayList<>();
                for (String cmd : disabledConfig.getKeys(false)) {
                    String cmds = cmd.replace("%colon%", ":");
                    String permission = disabledConfig.getString(cmd + ".Permission");
                    if (e.getCommands().contains(cmds)) {
                        if (permission == null) {
                            permission = plugin.getConfig().getString("disabled_default_permission");
                        }
                        if (!p.hasPermission(permission) && !p.isOp()) {
                            blocked.add(cmds);
                        }
                    }
                }
            e.getCommands().removeAll(blocked);
        }
    }
}