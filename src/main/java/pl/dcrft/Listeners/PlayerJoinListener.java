package pl.dcrft.Listeners;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Managers.LanguageManager;
import pl.dcrft.Managers.Panel.PanelManager;
import pl.dcrft.Managers.Panel.PanelType;
import pl.dcrft.Managers.SessionManager;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;
import pl.dcrft.Utils.RoundUtil;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;


public class PlayerJoinListener implements Listener {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        SessionManager newSession = new SessionManager(e.getPlayer());
        SessionManager.list.add(newSession);

        PanelManager.updatePanels();
        if(p.hasPermission("panel.adm")){
            new PanelManager().showRepeatingPanel(p, PanelType.ADMIN);
        }
        else if(p.hasPermission("panel.mod")) {
            new PanelManager().showRepeatingPanel(p, PanelType.MOD);
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
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                try {
                    DatabaseManager.openConnection();
                    Statement statement = DatabaseManager.connection.createStatement();
                    statement.executeUpdate("UPDATE " + DatabaseManager.table_bungee + " SET online='teraz' WHERE nick = '" + e.getPlayer().getName() + "'");

                    statement.close();
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

                    String kills = String.valueOf(p.getStatistic(Statistic.PLAYER_KILLS));
                    String deaths = String.valueOf(p.getStatistic(Statistic.DEATHS));

                    //Statistic expansion doubles the mine_block value, see https://github.com/PlaceholderAPI/Statistics-Expansion/issues/13
                    //So we're gonna divide it by 2 as a workaround :p
                    String blocks = String.valueOf(Integer.parseInt(PlaceholderAPI.setPlaceholders(p, "%statistic_mine_block%")) / 2);

                    int time = p.getStatistic(Statistic.PLAY_ONE_MINUTE);

                    String timeplayed =  PlaceholderAPI.setPlaceholders(p, "%statistic_time_played%");

                    Statement statement1 = DatabaseManager.connection.createStatement();

                    if (plugin.isSkyblock) {

                        String poziom = "0";
                        if (SuperiorSkyblockAPI.getPlayer(p).getIsland() != null){
                            poziom = SuperiorSkyblockAPI.getPlayer(p).getIsland().getIslandLevel().toString();
                        }

                        UUID uuid = p.getUniqueId();

                        String kasa = String.valueOf(Economy.getMoneyExact(uuid));


                        update = "UPDATE `" + DatabaseManager.table + "` SET kille = '" + kills + "', dedy = '" + deaths + "', kdr = '" + kdr + "', poziom = '" + poziom + "', kasa = '" + kasa + "', czasgry = '" + timeplayed + "' WHERE nick = '" + e.getPlayer().getName() + "'";
                    } else {
                        update = "UPDATE `" + DatabaseManager.table + "` SET kille = '" + kills + "', dedy = '" + deaths + "', kdr = '" + kdr + "', bloki = '" + blocks + "', czasgry = '" + timeplayed + "' WHERE nick = '" + e.getPlayer().getName() + "'";
                    }

                    statement1.executeUpdate(update);

                    statement1.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    ErrorUtil.logError(ErrorReason.DATABASE);
                } catch (UserDoesNotExistException e2) {
                    e2.printStackTrace();
                    ErrorUtil.logError(ErrorReason.OTHER);
                }

            }, 20L);
        }
    }
}
