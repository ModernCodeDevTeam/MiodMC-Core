package pl.dcrft.Managers.Statistic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.dcrft.DragonCraftCore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static pl.dcrft.Managers.DatabaseManager.*;

public class StatisticManager {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();
    private static ResultSet ogol;
    private static ResultSet server;
    private static boolean val;
    private static boolean val1;

    private static String kills;
    private static String deaths;
    private static String kdr;
    private static String rank;
    private static String blocks;
    private static String marry;

    private static String since;
    private static String online;
    private static String server_online;

    public static boolean checkPlayer(Player p){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    Statement statement = connection.createStatement();
                    ogol = statement.executeQuery("SELECT * FROM `" + table_bungee + "` WHERE nick = '" + p.getName() + "'");
                    server = statement.executeQuery("SELECT * FROM `" + table + "` WHERE nick = '" + p.getName() + "'");
                    val=ogol.next();
                    val1=ogol.next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        if(!val || !val1){
            return false;
        }
        return true;
    }

    public static String getStatistic(Player p, StatisticType type){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    Statement statement = connection.createStatement();
                    ogol = statement.executeQuery("SELECT * FROM `" + table_bungee + "` WHERE nick = '" + p.getName() + "'");
                    server = statement.executeQuery("SELECT * FROM `" + table + "` WHERE nick = '" + p.getName() + "'");
                    val=ogol.next();
                    val1=ogol.next();

                    online = ogol.getString("online");
                    since = ogol.getString("since");
                    if (online == null) {
                        online = "?";
                    }
                    if (since == null) {
                        since = "?";
                    }
                    server_online = ogol.getString("serwer_online");
                    kills = server.getString("kille");
                    deaths = server.getString("dedy");
                    kdr = server.getString("kdr");
                    rank = server.getString("ranga");
                    blocks = server.getString("bloki");
                    marry = server.getString("slub");
                    if (server_online.equalsIgnoreCase("lobby")) {
                        server_online = "Lobby";
                    }
                    if (server_online.equalsIgnoreCase("s12")) {
                        server_online = "Survival 1.12";
                    }
                    if (server_online.equalsIgnoreCase("s16")) {
                        server_online = "Survival 1.16";
                    }
                    if (server_online.equalsIgnoreCase("pvp")) {
                        server_online = "PvP";
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        if(!val || !val1){
            return "error";
        }
        else {
            switch (type){
                case KILLS:
                    return kills;
                case DEATHS:
                    return deaths;
                case KDR:
                    return kdr;
                case RANK:
                    return rank;
                case BLOCKS:
                    return blocks;
                case MARRY:
                    return marry;
                case SINCE:
                    return since;
                case ONLINE:
                    return online;
                case SERVER_ONLINE:
                    return server_online;
            }
        }
        return "error";
    }
}
