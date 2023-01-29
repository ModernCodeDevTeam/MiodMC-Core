package pl.dcrft.Managers.Statistic;

import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class StatisticManager {

    public static boolean checkPlayer(String p){
        boolean val = false, val1 = false;
                try {
                    DatabaseManager.openConnection();
                    ResultSet ogol;
                    Statement o = DatabaseManager.connection.createStatement();
                    ogol = o.executeQuery("SELECT * FROM `" + DatabaseManager.table_bungee + "` WHERE nick = '" + p + "'");
                    val = ogol.next();

                    ResultSet server;
                    Statement s = DatabaseManager.connection.createStatement();
                    server = s.executeQuery("SELECT * FROM `" + DatabaseManager.table + "` WHERE nick = '" + p + "'");
                    val1 = server.next();
                    ogol.close();
                    server.close();
                    o.close();
                    s.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        return val && val1;
    }

    public static HashMap<StatisticType, String> getStatistics(String p){

        DatabaseManager.openConnection();

        boolean val;

        String kills;
        String deaths;
        String kdr;
        String rank;
        String blocks;
        String timeplayed;
        String marry;

        String since;
        String online;
        HashMap<StatisticType, String> result = new HashMap<>();
            try {

                ResultSet rs;
                Statement statement = DatabaseManager.connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM `" + DatabaseManager.table + "` INNER JOIN `" + DatabaseManager.table_bungee + "` ON " + DatabaseManager.table + ".nick = " + DatabaseManager.table_bungee + ".nick WHERE " + DatabaseManager.table +  ".nick = '" + p + "'");
                val=rs.next();
                while(val){
                    online = rs.getString("online");
                    since = rs.getString("since");
                    rank = rs.getString("ranga");
                    if (online == null) {
                        online = "?";
                    }
                    if (since == null) {
                        since = "?";
                    }
                        kills = rs.getString("kille");
                        deaths = rs.getString("dedy");
                        kdr = rs.getString("kdr");
                        blocks = rs.getString("bloki");
                        timeplayed = rs.getString("czasgry");
                        marry = rs.getString("slub");

                    result.put(StatisticType.KILLS, kills);
                    result.put(StatisticType.DEATHS, deaths);
                    result.put(StatisticType.KDR, kdr);
                    result.put(StatisticType.RANK, rank);
                    result.put(StatisticType.BLOCKS, blocks);
                    result.put(StatisticType.TIMEPLAYED, timeplayed);
                    result.put(StatisticType.MARRY, marry);

                    result.put(StatisticType.SINCE, since);
                    result.put(StatisticType.ONLINE, online);
                    return result;
                }
                rs.close();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
                ErrorUtil.logError(ErrorReason.DATABASE);
            }
        return null;
    }
}
