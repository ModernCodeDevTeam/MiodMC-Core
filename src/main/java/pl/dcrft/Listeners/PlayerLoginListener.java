package pl.dcrft.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import pl.dcrft.DragonCraftCore;

import static pl.dcrft.Managers.LanguageManager.getMessage;


public class PlayerLoginListener implements Listener {
    private final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        if(!event.getAddress().toString().equals("/" + plugin.getConfig().getString("bungee.ip"))){
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("bungee.kick_message")));
            return;
        }

        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            Player player = event.getPlayer();

            if (player.hasPermission("vipslot.allow")) {
                event.allow();
            } else {
                event.setKickMessage(getMessage("prefix") + getMessage("server_full"));
            }
        }

    }
}
