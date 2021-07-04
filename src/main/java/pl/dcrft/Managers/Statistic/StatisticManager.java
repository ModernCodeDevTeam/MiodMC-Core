package pl.dcrft.Managers.Statistic;

import org.bukkit.Bukkit;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;
import pl.dcrft.Utils.Error.ErrorUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static pl.dcrft.Managers.DatabaseManager.*;

public class StatisticManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static boolean checkPlayer(String p){
        boolean val = false, val1 = false;
                try {
                    openConnection();
                    ResultSet ogol;
                    Statement o = connection.createStatement();
                    ogol = o.executeQuery("SELECT * FROM `" + table_bungee + "` WHERE nick = '" + p + "'");
                    val = ogol.next();

                    ResultSet server;
                    Statement s = connection.createStatement();
                    server = s.executeQuery("SELECT * FROM `" + table + "` WHERE nick = '" + p + "'");
                    val1 = server.next();
                    s.close();
                    o.close();
                    ogol.close();
                    server.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        return val && val1;
    }

    public static HashMap<StatisticType, String> getStatistics(String p){

        openConnection();

        boolean val;

        String kills;
        String deaths;
        String kdr;
        String rank;
        String blocks;
        String marry;

        String since;
        String online;
        String server_online;
        HashMap<StatisticType, String> result = new HashMap<>();
            try {

                ResultSet rs;
                Statement statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM `" + table + "` INNER JOIN `" + table_bungee + "` ON " + table + ".nick = " + table_bungee + ".nick WHERE " + table +  ".nick = '" + p + "'");
                val=rs.next();
                while(val){
                    online = rs.getString("online");
                    since = rs.getString("since");
                    server_online = rs.getString("serwer_online");
                    if (online == null) {
                        online = "?";
                    }
                    if (since == null) {
                        since = "?";
                    }
                        kills = rs.getString("kille");
                        deaths = rs.getString("dedy");
                        kdr = rs.getString("kdr");
                        rank = rs.getString("ranga");
                        blocks = rs.getString("bloki");
                        marry = rs.getString("slub");
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
                    result.put(StatisticType.KILLS, kills);
                    result.put(StatisticType.DEATHS, deaths);
                    result.put(StatisticType.KDR, kdr);
                    result.put(StatisticType.RANK, rank);
                    result.put(StatisticType.BLOCKS, blocks);
                    result.put(StatisticType.MARRY, marry);
                    result.put(StatisticType.SINCE, since);
                    result.put(StatisticType.ONLINE, online);
                    result.put(StatisticType.SERVER_ONLINE, server_online);
                    return result;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                ErrorUtil.logError(ErrorReason.DATABASE);
            }
        return null;
    }
}
