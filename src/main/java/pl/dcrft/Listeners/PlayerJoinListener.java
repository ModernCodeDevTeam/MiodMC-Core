package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.Language.LanguageManager;
import pl.dcrft.Managers.Panel.PanelType;
import pl.dcrft.Managers.SessionManager;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static pl.dcrft.Managers.ConfigManager.getDataFile;
import static pl.dcrft.Managers.DatabaseManager.*;
import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;
import static pl.dcrft.Managers.Panel.PanelManager.showRepeatingPanel;
import static pl.dcrft.Managers.Panel.PanelManager.updatePanels;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;
import static pl.dcrft.Utils.RoundUtil.round;

import static pl.dcrft.Managers.SessionManager.list;

public class PlayerJoinListener implements Listener {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        SessionManager newSession = new SessionManager(e.getPlayer());
        list.add(newSession);
        @NotNull List<Integer> sver = plugin.getConfig().getIntegerList("server.supported_versions");
        int pver = e.getPlayer().getProtocolVersion();
        if(!sver.contains(pver)){
            sendPrefixedMessage(p, "version_warning");
        }
        updatePanels();

        if(!e.getPlayer().hasPlayedBefore()) {
            getDataFile().set("najnowszy", e.getPlayer().getName());
            ConfigManager.saveData();
        }
        if (e.getPlayer().hasPermission("mod.chat")) {
            if(!getDataFile().contains(e.getPlayer().getName())){
                getDataFile().set(e.getPlayer().getName() + ":", null);
                getDataFile().set(e.getPlayer().getName() + ".modchat" , false);
                getDataFile().set(e.getPlayer().getName() + ".adminchat" , false);
                ConfigManager.saveData();
            }
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat")) {
                getDataFile().set(e.getPlayer().getName() + ".modchat", false);
                ConfigManager.saveData();
                return;
            }
        }
        if (e.getPlayer().hasPermission("admin.chat")) {
            if(!getDataFile().contains(e.getPlayer().getName())){
                getDataFile().set(e.getPlayer().getName() + ":", null);
                getDataFile().set(e.getPlayer().getName() + ".modchat" , false);
                getDataFile().set(e.getPlayer().getName() + ".adminchat" , false);
                ConfigManager.saveData();
            }
            if (!getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat")) {
                getDataFile().set(e.getPlayer().getName() + ".adminchat", true);
                e.getPlayer().sendMessage(LanguageManager.getMessage("staffchat.modchat.title") + " " + LanguageManager.getMessage("staffchat.auto_enabled"));
                ConfigManager.saveData();
                return;
            }
        }
        if (!e.getPlayer().hasPermission("pt.adm")) {
            getDataFile().set(e.getPlayer().getName() + ".online", null);
            ConfigManager.saveData();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    openConnection();
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE " + table_bungee + " SET online='teraz', serwer_online='" + plugin.getConfig().getString("server.name") + "' WHERE nick = '" + e.getPlayer().getName() + "'");

        int kille = Integer.parseInt(PlaceholderAPI.setPlaceholders(e.getPlayer(), "%statistic_player_kills%"));
        int dedy = Integer.parseInt(PlaceholderAPI.setPlaceholders(e.getPlayer(), "%statistic_deaths%"));
        float kdr;
        String ranga;
        String update;
        if (dedy == 0) {
            kdr = (float)kille;
        } else if (kille == 0) {
            kdr = 0.0F;
        } else {
            kdr = (float)kille / (float)dedy;
        }

        kdr = round(kdr, 2);
        ranga = PlaceholderAPI.setPlaceholders(e.getPlayer(), "%vault_rank%");

        if (ranga.equalsIgnoreCase("vip") || ranga.equalsIgnoreCase("vip+") || ranga.equalsIgnoreCase("svip") || ranga.equalsIgnoreCase("svip+") || ranga.equalsIgnoreCase("mvip") || ranga.equalsIgnoreCase("mvip+") || ranga.equalsIgnoreCase("evip") || ranga.equalsIgnoreCase("evip+")) {
            ranga = ranga.toUpperCase();
        }
        else if (ranga.equalsIgnoreCase("default") || ranga.equalsIgnoreCase("pomocnik") || ranga.equalsIgnoreCase("moderator")) {
            ranga = ranga.substring(0, 1).toUpperCase() + ranga.substring(1);
        }
        else if (ranga.equalsIgnoreCase("youtuber")) {
            ranga = "YouTuber";
        }
        else if (ranga.equalsIgnoreCase("w?a?ciciel") || ranga.equalsIgnoreCase("admin") || ranga.equalsIgnoreCase("viceadministrator")) {
            return;
        }

        update = PlaceholderAPI.setPlaceholders(e.getPlayer(), "UPDATE `" + table + "` SET kille = '%statistic_player_kills%', dedy = '%statistic_deaths%', kdr = '" + kdr + "', ranga = '" + ranga + "', bloki = '%statistic_mine_block%' WHERE nick = '" + e.getPlayer().getName() + "'");
        statement.executeUpdate(update);

        statement.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logError(ErrorReason.DATABASE);
                }

            });
        }
    }
}
