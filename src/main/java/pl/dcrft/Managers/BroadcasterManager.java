package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Language.LanguageManager;

import java.util.Random;

public class BroadcasterManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static void startBroadcast(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Random rand = new Random();
            String randomElement = LanguageManager.getMessageList("broadcast").get(rand.nextInt(LanguageManager.getMessageList("broadcast").size()));
            MessageManager.broadcastPrefixed(randomElement);
        }, 20L, Integer.parseInt(plugin.getConfig().getString("broadcast.cooldown")) * 20L);
    }
}
