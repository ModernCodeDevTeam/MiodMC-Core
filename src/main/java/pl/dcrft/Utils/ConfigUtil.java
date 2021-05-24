package pl.dcrft.Utils;

import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.LanguageManager.LanguageManager;
import pl.dcrft.Managers.MessageManager;

import java.io.File;

import static pl.dcrft.Managers.ConfigManger.*;
import static pl.dcrft.Managers.LanguageManager.LanguageManager.load;
import static pl.dcrft.Managers.LanguageManager.LanguageManager.loadFile;

public class ConfigUtil {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();
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
            load();
            loadFile();
        }
}
