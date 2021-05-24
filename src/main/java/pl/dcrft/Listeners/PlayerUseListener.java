package pl.dcrft.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.dcrft.DragonCraftCore;

import static pl.dcrft.Managers.ConfigManger.getDataFile;
import static pl.dcrft.Managers.DataManager.saveData;

import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;
import static pl.dcrft.Managers.SessionManager.list;

public class PlayerUseListener implements Listener {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;

    @EventHandler(priority= EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent e){
        Player p = e.getPlayer();
        for (pl.dcrft.Managers.SessionManager sessionManager : list) {
            if (p.getUniqueId() == sessionManager.getPlayer().getUniqueId()) {
                sessionManager.resetMinute();
                break;
            }
        }
        if(e.getClickedBlock() != null && e.getClickedBlock().getType() != null && e.getClickedBlock().getType().isBlock() && e.getClickedBlock().getType() == Material.LEVER) {
            if(Integer.parseInt(plugin.getConfig().getString("cooldown_lever")) > 0){
                e.setCancelled(true);
                sendPrefixedMessage(p, "lever_cooldown");
                return;
            }
            else {
                getDataFile().set("cooldown_lever", 1);
                saveData();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        getDataFile().set("cooldown_lever", 0);
                        saveData();
                    }
                }, 10L);
                return;
            }
        }
        return;
    }
}
