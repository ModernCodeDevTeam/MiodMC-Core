package pl.dcrft.Managers;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;
import pl.dcrft.Utils.Error.ErrorUtil;

import java.io.File;
import java.io.IOException;

import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class ConfigManger {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;

    public static File customConfigFile;
    public static FileConfiguration customConfig;

    public static File dataFileFile;
    public static FileConfiguration dataFile;

    public static File messagesFileFile;
    public static FileConfiguration messagesFile;

    public static void CheckConfig() {
        if (plugin.getConfig().get("pomoc") == null) {
            plugin.getConfig().set("pomoc", "brak");
            plugin.saveConfig();
            plugin.reloadConfig();
        }
    }
    public static FileConfiguration getCustomConfig() {
        return customConfig;
    }

    public static void createCustomConfig() {
        customConfigFile = new File(plugin.getDataFolder(), "gracz.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource("gracz.yml", false);
        }

        customConfig = new YamlConfiguration();

        try {
            customConfig.load(customConfigFile);
        } catch (InvalidConfigurationException | IOException var2) {
            logError(ErrorReason.DATA);
            var2.printStackTrace();
        }

    }

    public static FileConfiguration getDataFile() {
        return dataFile;
    }

    public static void createDataFile() {
        dataFileFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFileFile.exists()) {
            dataFileFile.getParentFile().mkdirs();
            plugin.saveResource("data.yml", false);
        }

        dataFile = new YamlConfiguration();

        try {
            dataFile.load(dataFileFile);
        } catch (InvalidConfigurationException | IOException var2) {
            logError(ErrorReason.DATA);
            var2.printStackTrace();
        }

    }

    public static FileConfiguration getMessagesFile() {
        return messagesFile;
    }

    public static void createMessagesFile() {
        messagesFileFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFileFile.exists()) {
            messagesFileFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }

        messagesFile = new YamlConfiguration();

        try {
            messagesFile.load(messagesFileFile);
        } catch (InvalidConfigurationException | IOException var2) {
            logError(ErrorReason.MESSAGES);
            var2.printStackTrace();
        }

    }

}
