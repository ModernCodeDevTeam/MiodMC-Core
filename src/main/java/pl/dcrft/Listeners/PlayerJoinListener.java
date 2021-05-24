package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.SessionManager;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static pl.dcrft.Managers.ConfigManger.getDataFile;
import static pl.dcrft.Managers.ConnectionManager.*;
import static pl.dcrft.Managers.DataManager.saveData;
import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;
import static pl.dcrft.Utils.RoundUtil.round;

import static pl.dcrft.Managers.SessionManager.list;

public class PlayerJoinListener implements Listener {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        SessionManager newSession = new SessionManager(e.getPlayer());
        list.add(newSession);
        //TODO informacja o wersji tylko jesli jest inna niz zalecana
        @NotNull List<Integer> sver = plugin.getConfig().getIntegerList("serwer.wersje");
        int pver = e.getPlayer().getProtocolVersion();
        if(!sver.contains(pver)){
            sendPrefixedMessage(p, "version_warning");
        }

        if(!e.getPlayer().hasPlayedBefore()) {
            getDataFile().set("najnowszy", e.getPlayer().getName());
            saveData();
        }
        if (e.getPlayer().hasPermission("mod.chat")) {
            if(!getDataFile().contains(e.getPlayer().getName())){
                getDataFile().set(e.getPlayer().getName() + ":", null);
                getDataFile().set(e.getPlayer().getName() + ".modchat" , false);
                getDataFile().set(e.getPlayer().getName() + ".adminchat" , false);
                saveData();
            }
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat") == true) {
                getDataFile().set(e.getPlayer().getName() + ".modchat", false);
                saveData();
                return;
            }
        }
        if (e.getPlayer().hasPermission("admin.chat")) {
            if(!getDataFile().contains(e.getPlayer().getName())){
                getDataFile().set(e.getPlayer().getName() + ":", null);
                getDataFile().set(e.getPlayer().getName() + ".modchat" , false);
                getDataFile().set(e.getPlayer().getName() + ".adminchat" , false);
                saveData();
            }
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat") == false) {
                getDataFile().set(e.getPlayer().getName() + ".adminchat", true);
                e.getPlayer().sendMessage("§c§lAdmin§4§lChat §e» §aAutomatycznie włączono czat.");
                saveData();
                return;
            }
        }
        if (!e.getPlayer().hasPermission("pt.adm")) {
            getDataFile().set(e.getPlayer().getName() + ".online", null);
            saveData();
            openConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE staty_ogolem SET online='teraz', serwer_online='" + plugin.getConfig().getString("nazwa_serwera") + "' WHERE nick = '" + e.getPlayer().getName() + "'");
            int kille;
            int dedy;
            float kdr;
            String ranga;
            String update;
            kille = Integer.parseInt(PlaceholderAPI.setPlaceholders(e.getPlayer(), "%statistic_player_kills%"));
            dedy = Integer.parseInt(PlaceholderAPI.setPlaceholders(e.getPlayer(), "%statistic_deaths%"));
            if (dedy == 0) {
                kdr = (float)kille;
            } else if (kille == 0) {
                kdr = 0.0F;
            } else {
                kdr = (float)kille / (float)dedy;
            }

            kdr = round(kdr, 2);
            ranga = PlaceholderAPI.setPlaceholders(e.getPlayer(), "%vault_rank%");
            if (ranga.equalsIgnoreCase("default")) {
                ranga = "Gracz";
            }

            if (ranga.equalsIgnoreCase("vip")) {
                ranga = "VIP";
            }

            if (ranga.equalsIgnoreCase("vip+")) {
                ranga = "VIP+";
            }

            if (ranga.equalsIgnoreCase("svip")) {
                ranga = "SVIP";
            }

            if (ranga.equalsIgnoreCase("svip+")) {
                ranga = "SVIP+";
            }

            if (ranga.equalsIgnoreCase("mvip")) {
                ranga = "MVIP";
            }

            if (ranga.equalsIgnoreCase("mvip+")) {
                ranga = "MVIP+";
            }

            if (ranga.equalsIgnoreCase("evip")) {
                ranga = "EVIP";
            }

            if (ranga.equalsIgnoreCase("evip+")) {
                ranga = "EVIP+";
            }

            if (ranga.equalsIgnoreCase("pomocnik")) {
                ranga = "Pomocnik";
            }

            if (ranga.equalsIgnoreCase("moderator")) {
                ranga = "Moderator";
            }

            if (ranga.equalsIgnoreCase("youtuber")) {
                ranga = "YouTuber";
            }

            if (ranga.equalsIgnoreCase("w?a?ciciel") || ranga.equalsIgnoreCase("admin") || ranga.equalsIgnoreCase("viceadministrator")) {
                return;
            }

            update = PlaceholderAPI.setPlaceholders(e.getPlayer(), "UPDATE `" + tabela + "` SET kille = '%statistic_player_kills%', dedy = '%statistic_deaths%', kdr = '" + kdr + "', ranga = '" + ranga + "', bloki = '%statistic_mine_block%' WHERE nick = '" + e.getPlayer().getName() + "'");
            statement.executeUpdate(update);

            statement.close();
        }
    }
}
