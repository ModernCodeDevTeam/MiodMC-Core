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
import pl.dcrft.Managers.MaintenanceManager;
import pl.dcrft.Managers.MessageManager;

import java.util.Arrays;

public class CommandPreprocessListener implements Listener {
    private static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().length() > 1 && e.getMessage().startsWith("/")) {

            String[] args = e.getMessage().split(" ");
            args = Arrays.copyOfRange(args, 1, args.length);

            FileConfiguration disabledConfig = ConfigManager.getDisabledFile();

            Player p = e.getPlayer();
            String command = e.getMessage().substring(1);
            command = command.split(" ")[0].replace(":", "%colon%");

            if (disabledConfig.getKeys(false) != null) {
                if (disabledConfig.getKeys(false).contains(command)) {
                    for (String cmd : disabledConfig.getKeys(false)) {
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

            if(command.equalsIgnoreCase("restart")){
                e.setCancelled(true);
                if (p.hasPermission("r.adm")) {
                    if (args.length == 0) {
                        MaintenanceManager.restartServer();
                    } else {
                        if (!args[0].chars().allMatch(Character::isDigit) || Integer.parseInt(args[0]) < 1) {
                            MessageManager.sendPrefixedMessage(p, "maintenance.wrong_value");
                        } else {
                            MaintenanceManager.restartServer(Integer.parseInt(args[0]));
                        }
                    }
                } else {
                    return;
                }
            }
            else if (command.equalsIgnoreCase("stop")) {
                if (p.hasPermission("r.adm")) {
                    if (args.length == 0) {
                        MaintenanceManager.stopServer();
                    } else {
                        if (!args[0].chars().allMatch(Character::isDigit) || Integer.parseInt(args[0]) < 1) {
                            MessageManager.sendPrefixedMessage(p, "maintenance.wrong_value");
                        } else {
                            MaintenanceManager.stopServer(Integer.parseInt(args[0]));
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }
}
