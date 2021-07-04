package pl.dcrft;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.dcrft.Listeners.*;
import pl.dcrft.Listeners.Anvil.AnvilBreakListener;
import pl.dcrft.Listeners.Anvil.AnvilDamageListener;
import pl.dcrft.Managers.CommandManager;
import pl.dcrft.Managers.LanguageManager;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.dcrft.Managers.BroadcasterManager.startBroadcast;
import static pl.dcrft.Managers.DatabaseManager.*;
import static pl.dcrft.Managers.Panel.PanelManager.updatePanels;
import static pl.dcrft.Utils.ConfigUtil.initializeFiles;
import static pl.dcrft.Utils.DatabaseUtil.initializeTable;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class DragonCraftCore extends JavaPlugin implements Listener, CommandExecutor {
    private static DragonCraftCore instance;
    public static LuckPerms luckPerms;

    public static DragonCraftCore getInstance() {
        return instance;
    }

    public Map<String, Object> filters;

    public DragonCraftCore() {
        this.filters = new HashMap<>();
    }

    public void onEnable() {
        instance = this;

        initializeFiles();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AdvancedBanWarnListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new InvetoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerUseListener(), this);
        getServer().getPluginManager().registerEvents(new CommandPreprocessListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilDamageListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilBreakListener(), this);

        getLogger().info(LanguageManager.getMessage("plugin.header"));
        getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        getLogger().info(LanguageManager.getMessage("plugin.enabled") + getDescription().getVersion());
        getLogger().info(LanguageManager.getMessage("plugin.footer"));

        openConnection();

        this.filters = this.getConfig().getConfigurationSection("filters").getValues(true);

        List<Command> commands = PluginCommandYamlParser.parse(this);
        for (Command command : commands) {
            getCommand(command.getName()).setExecutor(new CommandManager());
        }
        startBroadcast();
        initializeTable(table);
        updatePanels();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();

        }
    }

    public void onDisable() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logError(ErrorReason.DATABASE);
        }

        getLogger().info(LanguageManager.getMessage("plugin.header"));
        getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        getLogger().info(LanguageManager.getMessage("plugin.disabled") + getDescription().getVersion());
        getLogger().info(LanguageManager.getMessage("plugin.footer"));

    }


}
