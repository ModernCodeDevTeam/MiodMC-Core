package pl.dcrft.Utils;

import pl.dcrft.DragonCraftCore;

import java.io.File;

import static pl.dcrft.Managers.ConfigManager.*;
import static pl.dcrft.Managers.Language.LanguageManager.load;

public class ConfigUtil {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();
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

            createMessagesFile();
            createCustomConfig();
            createDataFile();
            load();
        }
}
