package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.*;
import pl.dcrft.Managers.Panel.PanelManager;
import pl.dcrft.Managers.Panel.PanelType;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;
import pl.dcrft.Utils.RoundUtil;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.List;



public class PlayerJoinListener implements Listener {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        for (String s : ConfigManager.getDataFile().getStringList("players." + p.getName() + ".znajomi")) {
            if(Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s).isOnline()) {
                Bukkit.getPlayer(s).sendMessage(LanguageManager.getMessage("prefix") + MessageFormat.format(LanguageManager.getMessage("friends.join"), p.getName()));
            }
        }


        SessionManager newSession = new SessionManager(e.getPlayer());
        SessionManager.list.add(newSession);
        List<Integer> sver = plugin.getConfig().getIntegerList("server.supported_versions");
        int pver = e.getPlayer().getProtocolVersion();
        if (!sver.contains(pver)) {
            MessageManager.sendPrefixedMessage(p, "version_warning");
        }

        PanelManager.updatePanels();

        if(p.hasPermission("panel.adm")){
            PanelManager.showRepeatingPanel(p, PanelType.ADMIN);
        }
        else if(p.hasPermission("panel.mod")) {
            PanelManager.showRepeatingPanel(p, PanelType.MOD);
        }


        if (!e.getPlayer().hasPlayedBefore()) {
            ConfigManager.getDataFile().set("najnowszy", e.getPlayer().getName());
            ConfigManager.saveData();
        }
        if (e.getPlayer().hasPermission("mod.chat")) {
            if (!ConfigManager.getDataFile().contains("players." + e.getPlayer().getName())) {
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ":", null);
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".modchat", false);
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".adminchat", false);
                ConfigManager.saveData();
            }
            if (ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".modchat")) {
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".modchat", false);
                ConfigManager.saveData();
                return;
            }
        }
        if (e.getPlayer().hasPermission("admin.chat")) {
            if (!ConfigManager.getDataFile().contains("players." + e.getPlayer().getName())) {
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ":", null);
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".modchat", false);
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".adminchat", false);
                ConfigManager.saveData();
            }
            if (!ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".adminchat")) {
                ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".adminchat", true);
                e.getPlayer().sendMessage(LanguageManager.getMessage("staffchat.adminchat.title") + LanguageManager.getMessage("staffchat.adminchat.spacer") + LanguageManager.getMessage("staffchat.auto_enabled"));
                ConfigManager.saveData();
                return;
            }
        }
        if (!e.getPlayer().hasPermission("pt.adm")) {
            ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".online", null);
            ConfigManager.saveData();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    DatabaseManager.openConnection();
                    Statement statement = DatabaseManager.connection.createStatement();
                    statement.executeUpdate("UPDATE " + DatabaseManager.table_bungee + " SET online='teraz', serwer_online='" + plugin.getConfig().getString("server.name") + "' WHERE nick = '" + e.getPlayer().getName() + "'");

                    int kille = p.getStatistic(Statistic.PLAYER_KILLS);
                    int dedy = p.getStatistic(Statistic.DEATHS);
                    float kdr;
                    String ranga;
                    String update;
                    if (dedy == 0) {
                        kdr = (float) kille;
                    } else if (kille == 0) {
                        kdr = 0.0F;
                    } else {
                        kdr = (float) kille / (float) dedy;
                    }
                    kdr = RoundUtil.round(kdr, 2);
                    ranga = PlaceholderAPI.setPlaceholders(e.getPlayer(), "%vault_rank%");

                    if (ranga.equalsIgnoreCase("vip") || ranga.equalsIgnoreCase("vip+") || ranga.equalsIgnoreCase("svip") || ranga.equalsIgnoreCase("svip+") || ranga.equalsIgnoreCase("mvip") || ranga.equalsIgnoreCase("mvip+") || ranga.equalsIgnoreCase("evip") || ranga.equalsIgnoreCase("evip+")) {
                        ranga = ranga.toUpperCase();
                    } else if (ranga.equalsIgnoreCase("default") || ranga.equalsIgnoreCase("pomocnik") || ranga.equalsIgnoreCase("moderator")) {
                        ranga = ranga.substring(0, 1).toUpperCase() + ranga.substring(1);
                    } else if (ranga.equalsIgnoreCase("youtuber")) {
                        ranga = "YouTuber";
                    } else if (ranga.equalsIgnoreCase("w?a?ciciel") || ranga.equalsIgnoreCase("admin") || ranga.equalsIgnoreCase("viceadministrator")) {
                        return;
                    }

                    String kills = String.valueOf(p.getStatistic(Statistic.PLAYER_KILLS));
                    String deaths = String.valueOf(p.getStatistic(Statistic.DEATHS));
                    String blocks = PlaceholderAPI.setPlaceholders(p, "%statistic_mine_block%");

                    int time = p.getStatistic(Statistic.PLAY_ONE_MINUTE);

                    String timeplayed =  PlaceholderAPI.setPlaceholders(p, "%statistic_time_played%");


                    update = "UPDATE `" + DatabaseManager.table + "` SET kille = '" + kills + "', dedy = '" + deaths + "', kdr = '" + kdr + "', ranga = '" + ranga + "', bloki = '" + blocks + "', czasgry = '" + timeplayed + "' WHERE nick = '" + e.getPlayer().getName() + "'";
                    statement.executeUpdate(update);

                    statement.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    ErrorUtil.logError(ErrorReason.DATABASE);
                }

            });
        }
    }
}
