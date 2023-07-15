package pl.dcrft.Managers;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import pl.dcrft.DragonCraftCore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TopHologramManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    static HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);

    static int x = plugin.getConfig().getInt("hologram.x");
    static int y = plugin.getConfig().getInt("hologram.y");
    static int z = plugin.getConfig().getInt("hologram.z");
    static Location location = new Location(Bukkit.getServer().getWorld("world"), x, y, z);
    public static Hologram hologram = api.createHologram(location);
    public static void startBroadcast(){

        String server = plugin.getConfig().getString("server.type");
        int page = 0;

        String title = LanguageManager.getMessage("hologram.title");

        String kills = LanguageManager.getMessage("hologram.kills");
        String deaths = LanguageManager.getMessage("hologram.deaths");
        String kdr = LanguageManager.getMessage("hologram.kdr");
        String blocks = LanguageManager.getMessage("hologram.blocks");
        String level = LanguageManager.getMessage("hologram.islandlevel");
        String money = LanguageManager.getMessage("hologram.money");



        hologram.getLines().appendText("title");
        hologram.getLines().appendText("1");
        hologram.getLines().appendText("2");
        hologram.getLines().appendText("3");
        hologram.getLines().appendText("4");
        hologram.getLines().appendText("5");
        hologram.getLines().appendText("6");
        hologram.getLines().appendText("7");
        hologram.getLines().appendText("8");
        hologram.getLines().appendText("9");
        hologram.getLines().appendText("10");

        switch (server){
            case "survival":
                Bukkit.getScheduler().runTaskTimer(plugin, (Runnable) () -> {
                    List<String[]> survlist = getStatistics(server);
                    Bukkit.getScheduler().runTaskLater(plugin, (Runnable) () -> {
                        hologram.getLines().clear();
                        hologram.getLines().insertText(0, title +  kills);
                        int i = 0;
                        for (String s : survlist.get(0)) {
                            i++;
                            hologram.getLines().insertText(i, s);
                        }
                    }, 0);
                    Bukkit.getScheduler().runTaskLater(plugin, (Runnable) () -> {
                        hologram.getLines().clear();
                        hologram.getLines().insertText(0, title + deaths);
                        int i = 0;
                        for (String s : survlist.get(1)) {
                            i++;
                            hologram.getLines().insertText(i, s);
                        }
                    }, plugin.getConfig().getInt("hologram.cooldown") / 4L * 20L);
                    Bukkit.getScheduler().runTaskLater(plugin, (Runnable) () -> {
                        hologram.getLines().clear();
                        hologram.getLines().insertText(0, title + kdr);
                        int i = 0;
                        for (String s : survlist.get(2)) {
                            i++;
                            hologram.getLines().insertText(i, s);
                        }
                    }, plugin.getConfig().getInt("hologram.cooldown") / 2L * 20L);
                    Bukkit.getScheduler().runTaskLater(plugin, (Runnable) () -> {
                        hologram.getLines().clear();
                        hologram.getLines().insertText(0, title + blocks);
                        int i = 0;
                        for (String s : survlist.get(3)) {
                            i++;
                            hologram.getLines().insertText(i, s);
                        }
                    }, plugin.getConfig().getInt("hologram.cooldown") * 3L / 4L * 20L);

                }, 0L, plugin.getConfig().getInt("hologram.cooldown")*20L);
            break;
            case "skyblock":
                //TODO
                break;
        }
    }

    private static List<String[]> getStatistics(String server){

        List<String[]> finList = new ArrayList<>(10);

        DatabaseManager.openConnection();
        Statement statement = null;
        try {
            statement = DatabaseManager.connection.createStatement();
            String[] queries = new String[5];
            queries[0] = "SELECT nick, kille FROM staty_" + server + " ORDER BY kille DESC LIMIT 10";
            queries[1] = "SELECT nick, dedy FROM staty_" + server + " ORDER BY dedy DESC LIMIT 10";
            queries[2] = "SELECT nick, kdr FROM staty_" + server + " ORDER BY kdr DESC LIMIT 10";
            switch (server){
                case "survival":
                  queries[3] = "SELECT nick, bloki FROM staty_" + server + " ORDER BY bloki DESC LIMIT 10";
                  break;
                case "skyblock":
                  queries[3] = "SELECT nick, poziom FROM staty_" + server + " ORDER BY poziom DESC LIMIT 10";
                  queries[4] = "SELECT nick, kasa FROM staty_" + server + " ORDER BY kasa DESC LIMIT 10";
                  break;
            }
            for(String q : queries){
                int i = 0;
                if(q==null) break;
                ResultSet rs = statement.executeQuery(q);
                String[] list = new String[10];
                while(rs.next()){
                    list[i] = LanguageManager.getMessage("hologram.colors.nick") + rs.getString("nick") + ": " + LanguageManager.getMessage("hologram.colors.value") + rs.getString(2);
                    i++;
                }
                finList.add(list);
            }

            return finList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}

