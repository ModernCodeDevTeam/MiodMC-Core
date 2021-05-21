package pl.dcrft.Utils;

import pl.dcrft.DragonCraftCore;

import java.io.File;

import static pl.dcrft.Managers.ConfigManger.*;

public class ConfigUtil {
    private static DragonCraftCore plugin;
        public static void initializeFiles(){
            final File file = new File(plugin.getDataFolder() + File.separator + "config.yml");
            if (!file.exists()) {
                plugin.saveDefaultConfig();
            }
            else {
                CheckConfig();
                plugin.saveConfig();
                plugin.reloadConfig();
            }
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();

            createCustomConfig();
            createDataFile();
            createMessagesFile();
        }
}
