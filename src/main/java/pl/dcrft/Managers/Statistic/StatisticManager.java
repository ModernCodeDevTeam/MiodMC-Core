package pl.dcrft.Managers.Statistic;

import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class StatisticManager {

    public static boolean checkPlayer(String p) {
        boolean bungee = false, survi = false, sky = false;
                try {
                    DatabaseManager.openConnection();
                    ResultSet ogol;
                    Statement o = DatabaseManager.connection.createStatement();
                    ogol = o.executeQuery("SELECT * FROM `" + DatabaseManager.table_bungee + "` WHERE nick = '" + p + "'");
                    bungee = ogol.next();

                    ResultSet surv;
                    Statement su = DatabaseManager.connection.createStatement();
                    surv = su.executeQuery("SELECT * FROM `" + DatabaseManager.table_survival + "` WHERE nick = '" + p + "'");
                    survi = surv.next();

                    ResultSet skyb;
                    Statement sk = DatabaseManager.connection.createStatement();
                    skyb = sk.executeQuery("SELECT * FROM `" + DatabaseManager.table_skyblock + "` WHERE nick = '" + p + "'");
                    sky = skyb.next();

                    ogol.close();
                    surv.close();
                    skyb.close();
                    o.close();
                    su.close();
                    sk.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        return bungee && survi && sky;
    }

    public static HashMap<StatisticType, String> getStatistics(String p){

        DatabaseManager.openConnection();

        boolean val;

        String rank;
        String since;
        String online;

        String survival_kills;
        String survival_deaths;
        String survival_kdr;
        String survival_blocks;
        String survival_timeplayed;
        String survival_marry;


        String skyblock_kills;
        String skyblock_deaths;
        String skyblock_kdr;
        String skyblock_level;
        String skyblock_money;
        String skyblock_timeplayed;
        String skyblock_marry;


        HashMap<StatisticType, String> result = new HashMap<>();
            try {

                ResultSet rs;
                Statement statement = DatabaseManager.connection.createStatement();
                rs = statement.executeQuery("SELECT " + DatabaseManager.table_bungee +  ".nick AS ogolem_nick, " + DatabaseManager.table_bungee +  ".since AS ogolem_since, " + DatabaseManager.table_bungee +  ".online AS ogolem_online, " + DatabaseManager.table_survival +  ".nick AS survival_nick, " + DatabaseManager.table_survival +  ".kille AS survival_kille, " + DatabaseManager.table_survival +  ".dedy AS survival_dedy, " + DatabaseManager.table_survival +  ".kdr AS survival_kdr, " + DatabaseManager.table_bungee +  ".ranga AS ogolem_ranga, " + DatabaseManager.table_survival +  ".bloki AS survival_bloki, " + DatabaseManager.table_survival +  ".czasgry AS survival_czasgry, " + DatabaseManager.table_survival +  ".slub AS survival_slub," +
                        DatabaseManager.table_skyblock +  ".nick AS skyblock_nick, " + DatabaseManager.table_skyblock +  ".kille AS skyblock_kille, " + DatabaseManager.table_skyblock +  ".dedy AS skyblock_dedy, " + DatabaseManager.table_skyblock +  ".kdr AS skyblock_kdr, " + DatabaseManager.table_bungee +  ".ranga AS ogolem_ranga, " + DatabaseManager.table_skyblock +  ".poziom AS skyblock_poziom, " + DatabaseManager.table_skyblock +  ".kasa AS skyblock_kasa, " + DatabaseManager.table_skyblock +  ".czasgry AS skyblock_czasgry, " + DatabaseManager.table_skyblock +  ".slub AS skyblock_slub FROM " + DatabaseManager.table_bungee + " " +
                        "LEFT JOIN " + DatabaseManager.table_survival + " " +
                        "ON " + DatabaseManager.table_survival +  ".nick = " + DatabaseManager.table_bungee +  ".nick" + " " +
                        "LEFT JOIN " +  DatabaseManager.table_skyblock + " " +
                        "ON " +  DatabaseManager.table_skyblock + ".nick = " + DatabaseManager.table_bungee +  ".nick " + " " +
                        "WHERE " + DatabaseManager.table_skyblock +  ".nick = '" + p + "'");
                val=rs.next();
                while(val){
                    online = rs.getString("ogolem_online");
                    since = rs.getString("ogolem_since");
                    rank = rs.getString("ogolem_ranga");
                    if (online == null) {
                        online = "?";
                    }
                    if (since == null) {
                        since = "?";
                    }
                    survival_kills = rs.getString("survival_kille");
                    survival_deaths = rs.getString("survival_dedy");
                    survival_kdr = rs.getString("survival_kdr");
                    survival_blocks = rs.getString("survival_bloki");
                    survival_timeplayed = rs.getString("survival_czasgry");
                    survival_marry = rs.getString("survival_slub");

                    skyblock_kills = rs.getString("skyblock_kille");
                    skyblock_deaths = rs.getString("skyblock_dedy");
                    skyblock_kdr = rs.getString("skyblock_kdr");
                    skyblock_level = rs.getString("skyblock_poziom");
                    skyblock_money = rs.getString("skyblock_kasa");
                    skyblock_timeplayed = rs.getString("skyblock_czasgry");
                    skyblock_marry = rs.getString("skyblock_slub");


                    result.put(StatisticType.RANK, rank);
                    result.put(StatisticType.SINCE, since);
                    result.put(StatisticType.ONLINE, online);

                    result.put(StatisticType.SURVIVAL_KILLS, survival_kills);
                    result.put(StatisticType.SURVIVAL_DEATHS, survival_deaths);
                    result.put(StatisticType.SURVIVAL_KDR, survival_kdr);
                    result.put(StatisticType.SURVIVAL_BLOCKS, survival_blocks);
                    result.put(StatisticType.SURVIVAL_TIMEPLAYED, survival_timeplayed);
                    result.put(StatisticType.SURVIVAL_MARRY, survival_marry);

                    result.put(StatisticType.SKYBLOCK_KILLS, skyblock_kills);
                    result.put(StatisticType.SKYBLOCK_DEATHS, skyblock_deaths);
                    result.put(StatisticType.SKYBLOCK_KDR, skyblock_kdr);
                    result.put(StatisticType.SKYBLOCK_LEVEL, skyblock_level);
                    result.put(StatisticType.SKYBLOCK_MONEY, skyblock_money);
                    result.put(StatisticType.SKYBLOCK_TIMEPLAYED, skyblock_timeplayed);
                    result.put(StatisticType.SKYBLOCK_MARRY, skyblock_marry);


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
