package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pl.dcrft.DragonCraftCore;

import java.util.Random;

public class BroadcasterManager {
    public static DragonCraftCore plugin;
    public static void startBroadcast(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                final Random rand = new Random();
                final String randomElement = plugin.getConfig().getStringList("wiadomosci").get(rand.nextInt(plugin.getConfig().getStringList("wiadomosci").size()));
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("przedrostekwiadomosci")) + ChatColor.translateAlternateColorCodes('&', randomElement));
            }
        }, 20L, Integer.parseInt(plugin.getConfig().getString("cooldown")) * 20);
    }
}
