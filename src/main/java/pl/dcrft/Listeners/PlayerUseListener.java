package pl.dcrft.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.MessageManager;
import pl.dcrft.Managers.SessionManager;


public class PlayerUseListener implements Listener {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler(priority= EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent e){
        Player p = e.getPlayer();
        for (pl.dcrft.Managers.SessionManager sessionManager : SessionManager.list) {
            if (p.getUniqueId() == sessionManager.getPlayer().getUniqueId()) {
                sessionManager.resetMinute();
                break;
            }
        }
        if(e.getClickedBlock() != null && e.getClickedBlock().getType() != null && e.getClickedBlock().getType().isBlock() && e.getClickedBlock().getType() == Material.LEVER) {
            if(ConfigManager.getDataFile().getInt("cooldown_lever") > 0){
                e.setCancelled(true);
                MessageManager.sendPrefixedMessage(p, "lever_cooldown");
            }
            else {
                ConfigManager.getDataFile().set("cooldown_lever", 1);
                ConfigManager.saveData();
                Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                    ConfigManager.getDataFile().set("cooldown_lever", 0);
                    ConfigManager.saveData();
                }, 10L);
            }
        }
    }
}
