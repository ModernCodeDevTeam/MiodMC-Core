package pl.dcrft.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;

public class CommandPreprocessListener implements Listener {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().length() > 1 && e.getMessage().startsWith("/")) {
            FileConfiguration disabledConfig = ConfigManager.getDisabledFile();
            String command = e.getMessage();

            Player p = e.getPlayer();
            command = e.getMessage().substring(1);
            command = command.split(" ")[0];

            if (disabledConfig.getKeys(false) != null) {
                if (disabledConfig.getKeys(false).contains(command)) {
                    for (String cmd : disabledConfig.getKeys(false)) {
                        cmd.replace("%colon%", ":");
                        String permission = disabledConfig.getString(cmd + ".Permission");
                        if (command.equalsIgnoreCase(cmd)) {
                            if (permission == null) {
                                permission = plugin.getConfig().getString("disabled_default_permission");
                            }
                            if (!p.hasPermission(permission) && !p.isOp()) {
                                e.setCancelled(true);
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', disabledConfig.getString(cmd + ".Message")));
                                return;
                            }
                        }
                    }
                }
            }

            String aliasResult = plugin.getConfig().getString("aliases." + command);
            if (aliasResult != null) {
                String userArguments = e.getMessage().substring(command.length() + 1);
                e.setMessage(e.getMessage().substring(0, 1) + aliasResult + userArguments);
            }
        }
    }
}
