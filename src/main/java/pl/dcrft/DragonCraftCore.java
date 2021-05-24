package pl.dcrft;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.dcrft.Listeners.*;
import pl.dcrft.Managers.CommandManager;
import pl.dcrft.Managers.Panel.PanelType;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.*;
import java.util.*;

import static pl.dcrft.Managers.BroadcasterManager.startBroadcast;
import static pl.dcrft.Managers.DatabaseManager.*;
import static pl.dcrft.Managers.Panel.PanelManager.showPanel;
import static pl.dcrft.Utils.ConfigUtil.initializeFiles;
import static pl.dcrft.Utils.DatabaseUtil.initializeTable;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class DragonCraftCore extends JavaPlugin implements Listener, CommandExecutor {
    private static DragonCraftCore instance;

    public static DragonCraftCore getInstance() {
        return instance;
    }

    public Map<String, Object> filtry;
    public DragonCraftCore() {
        this.filtry = new HashMap<>();
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

        this.getLogger().info("§e--------------------------------------------");
        this.getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        this.getLogger().info("§aWłączono wersję §2" + getDescription().getVersion());
        this.getLogger().info("§e--------------------------------------------");

        openConnection();

        this.filtry = this.getConfig().getConfigurationSection("filtry").getValues(true);

        List<Command> commands = PluginCommandYamlParser.parse(this);
        for (Command command : commands) {
            getCommand(command.getName()).setExecutor(new CommandManager());
        }
        startBroadcast();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("panel.adm")) {
                showPanel(p, PanelType.ADMIN);
            }
            else if (p.hasPermission("panel.mod")) {
                showPanel(p, PanelType.MOD);
            }
        }
        initializeTable(table);
    }

    public void onDisable() {
        try {
            if(connection != null){ connection.close(); };
        } catch (SQLException e) {
            e.printStackTrace();
            logError(ErrorReason.DATABASE);
        }
        getLogger().info("§e--------------------------------------------");
        getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        getLogger().info("§cWyłączono wersję §2" + this.getDescription().getVersion());
        getLogger().info("§e--------------------------------------------");

    }



}
