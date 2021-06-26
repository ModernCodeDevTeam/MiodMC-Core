package pl.dcrft.Utils;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;

import java.io.File;

import static pl.dcrft.Managers.ConfigManager.*;
import static pl.dcrft.Managers.LanguageManager.load;

public class ConfigUtil {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static void initializeFiles() {
        final File file = new File(plugin.getDataFolder() + File.separator + "config.yml");
        if (!file.exists()) {
            plugin.saveDefaultConfig();
        } else {
            CheckConfig();
            plugin.saveConfig();
            plugin.reloadConfig();
        }
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        createMessagesFile();
        createCustomConfig();
        createDataFile();
        createDisabledFile();
        load();
    }

    public static void reloadFiles() {
        ConfigManager.saveData();

        plugin.reloadConfig();
        data = YamlConfiguration.loadConfiguration(dataFile);
        databaseConfig = YamlConfiguration.loadConfiguration(databaseConfigFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        disabledConfig = YamlConfiguration.loadConfiguration(disabledConfigFile);

        plugin.filters = plugin.getConfig().getConfigurationSection("filters").getValues(true);
    }
}
