package pl.dcrft.Managers.Panel;

import de.myzelyam.api.vanish.VanishAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import pl.dcbot.main.Bot;
import pl.dcrft.DragonCraftCore;

import java.awt.*;

import static pl.dcrft.DragonCraftCore.prefix;
import static pl.dcrft.Managers.ConfigManger.getDataFile;
import static pl.dcrft.Utils.RoundUtil.round;


public class PanelManager {

    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;
    public static String title;

    public static void showPanel(Player p, PanelType type){
        if(type == PanelType.MOD){
            title = prefix + "§a§lMod§2§lPanel";
        }
        else {
            title = prefix + "§c§lAdmin§4§lPanel";
        }
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                        ScoreboardManager manager = Bukkit.getScoreboardManager();
                        Scoreboard admpanel = manager.getNewScoreboard();
                        Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        objective.setDisplayName(title);
                    p.setScoreboard(emptyBoard);
                    Score nick = objective.getScore("§e» §6" + p.getName());
                    nick.setScore(16);
                    Score czat;
                    if (getDataFile().getString(p.getName() + ".adminchat") == "true") {
                        if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lA§4§lC §ei §a§lM§2§lC");
                        }
                        else {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lAdmin§4§lChat");
                        }
                    }
                    else if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                        czat = objective.getScore("§e» §6Czat §e» " + "§a§lMod§2§lChat");
                    }
                    else {
                        czat = objective.getScore("§e» §6Czat §e» " + "§ePubliczny");
                    }
                    czat.setScore(15);
                    Score gracze = objective.getScore("§e» §6Gracze §e» " + (Bukkit.getServer().getOnlinePlayers().size() - VanishAPI.getInvisiblePlayers().size()) + " §7[§f+" + VanishAPI.getInvisiblePlayers().size() + "§7]");
                    gracze.setScore(14);
                    Score ping = objective.getScore("§e» §6Ping §e» " + p.spigot().getPing() + "ms");
                    ping.setScore(13);
                    double itps;
                    itps = Math.round(Bukkit.getTPS()[0] * 100.0) / 100.0;
                    String wyd;
                    if (itps <= 8) {
                        wyd = "§4uhh";
                    }
                    else if (itps >= 8) {
                        wyd = "§4uhh";
                        if (itps >= 10) {
                            wyd = "§4tragiczna";
                            if (itps >= 14) {
                                wyd = "§4zła";
                                if (itps >= 16) {
                                    wyd = "§ckiepska";
                                    if (itps >= 18) {
                                        wyd = "§aok";
                                        if (itps >= 19) {
                                            wyd = "§2dobra";
                                            if (itps >= 20) {
                                                wyd = "§2super";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        wyd = "?";
                    }
                    Score wydajnosc = objective.getScore("§e» §6Wydajność §e» " + wyd);
                    wydajnosc.setScore(12);
                    Score tps = objective.getScore("§e» §6TPS §e» " + itps);
                    tps.setScore(11);
                    String kolor2 = "§2";
                    float ms = (float)Bukkit.getAverageTickTime();
                    if (ms <= 45 ) {
                        kolor2 = "§2";
                    }
                    if (ms >= 45) {
                        kolor2 = "§a";
                    }
                    if (ms >= 50) {
                        kolor2 = "§e";
                    }
                    if (ms >= 55) {
                        kolor2 = "§6";
                    }
                    if (ms >= 60) {
                        kolor2 = "§c";
                    }
                    if (ms >= 70) {
                        kolor2 = "§4";
                    }
                    Score mss = objective.getScore("§e» §6Średni MSPT §e» " + kolor2 + round(ms, 2) + "§ems");
                    mss.setScore(10);

                    Runtime r = Runtime.getRuntime();
                    long memUsed = ((r.totalMemory() / 1048576) - (r.freeMemory() / 1048576));
                    long memCala = (r.totalMemory() / 1048576);
                    long memFree = (r.freeMemory() / 1048576);

                    String kolor = "§a";
                    String powiadom = "";
                    if (memFree < 1000) {
                        kolor = "§a";
                        powiadom = "";
                    }
                    if (memFree < 800) {
                        kolor = "§e";
                        powiadom = "";
                    }
                    if (memFree < 600) {
                        kolor = "§6";
                        powiadom = "";
                    }
                    if (memFree < 400) {
                        kolor = "§c";
                        powiadom = "";
                    }
                    if (memFree < 200) {
                        kolor = "§4";
                        powiadom = "";
                    }
                    if (memFree < 100) {
                        kolor = "§4";
                        powiadom = "§c!";
                    }
                    else if (memFree >= 1000){
                        kolor = "§2";
                        powiadom = "";
                    }
                    Score ram = objective.getScore("§e» " + kolor + "§lRAM §e» §6" + memUsed + "§e/§6" + memCala + " " + powiadom);
                    ram.setScore(9);
                    Score uptime = objective.getScore("§e» §6Aktywny od §e» " + PlaceholderAPI.setPlaceholders(p, "%server_uptime%"));
                    uptime.setScore(8);
                    Score newest_t = objective.getScore("§e» §6Najnowszy gracz§e:");
                    newest_t.setScore(7);
                    Score newest = objective.getScore("§e» " + getDataFile().getString("najnowszy"));
                    newest.setScore(6);

                    p.setScoreboard(admpanel);
                    if (getDataFile().getBoolean(p.getName() + ".stream") == true) {
                        //do not set a new scoreboard if there is already the empty one
                        if(!p.getScoreboard().equals(emptyBoard)){
                            p.setScoreboard(emptyBoard);
                        }
                    }
            }
        }, 0L, 100L);
    }
    public static void updatePanel(Player p, PanelType type){
        if(type == PanelType.MOD){
            title = prefix + "§a§lMod§2§lPanel";
        }
        else {
            title = prefix + "§c§lAdmin§4§lPanel";
        }
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard admpanel = manager.getNewScoreboard();
                Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                Scoreboard emptyBoard = manager.getNewScoreboard();
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName(title);
                p.setScoreboard(emptyBoard);
                Score nick = objective.getScore("§e» §6" + p.getName());
                nick.setScore(16);
                Score czat;
                if (getDataFile().getString(p.getName() + ".adminchat") == "true") {
                    if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                        czat = objective.getScore("§e» §6Czat §e» " + "§c§lA§4§lC §ei §a§lM§2§lC");
                    }
                    else {
                        czat = objective.getScore("§e» §6Czat §e» " + "§c§lAdmin§4§lChat");
                    }
                }
                else if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                    czat = objective.getScore("§e» §6Czat §e» " + "§a§lMod§2§lChat");
                }
                else {
                    czat = objective.getScore("§e» §6Czat §e» " + "§ePubliczny");
                }
                czat.setScore(15);
                Score gracze = objective.getScore("§e» §6Gracze §e» " + (Bukkit.getServer().getOnlinePlayers().size() - VanishAPI.getInvisiblePlayers().size()) + " §7[§f+" + VanishAPI.getInvisiblePlayers().size() + "§7]");
                gracze.setScore(14);
                Score ping = objective.getScore("§e» §6Ping §e» " + p.spigot().getPing() + "ms");
                ping.setScore(13);
                double itps;
                itps = Math.round(Bukkit.getTPS()[0] * 100.0) / 100.0;
                String wyd;
                if (itps <= 8) {
                    wyd = "§4uhh";
                }
                else if (itps >= 8) {
                    wyd = "§4uhh";
                    if (itps >= 10) {
                        wyd = "§4tragiczna";
                        if (itps >= 14) {
                            wyd = "§4zła";
                            if (itps >= 16) {
                                wyd = "§ckiepska";
                                if (itps >= 18) {
                                    wyd = "§aok";
                                    if (itps >= 19) {
                                        wyd = "§2dobra";
                                        if (itps >= 20) {
                                            wyd = "§2super";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    wyd = "?";
                }
                Score wydajnosc = objective.getScore("§e» §6Wydajność §e» " + wyd);
                wydajnosc.setScore(12);
                Score tps = objective.getScore("§e» §6TPS §e» " + itps);
                tps.setScore(11);
                String kolor2 = "§2";
                float ms = (float)Bukkit.getAverageTickTime();
                if (ms <= 45 ) {
                    kolor2 = "§2";
                }
                if (ms >= 45) {
                    kolor2 = "§a";
                }
                if (ms >= 50) {
                    kolor2 = "§e";
                }
                if (ms >= 55) {
                    kolor2 = "§6";
                }
                if (ms >= 60) {
                    kolor2 = "§c";
                }
                if (ms >= 70) {
                    kolor2 = "§4";
                }
                Score mss = objective.getScore("§e» §6Średni MSPT §e» " + kolor2 + round(ms, 2) + "§ems");
                mss.setScore(10);

                Runtime r = Runtime.getRuntime();
                long memUsed = ((r.totalMemory() / 1048576) - (r.freeMemory() / 1048576));
                long memCala = (r.totalMemory() / 1048576);
                long memFree = (r.freeMemory() / 1048576);

                String kolor = "§a";
                String powiadom = "";
                if (memFree < 1000) {
                    kolor = "§a";
                    powiadom = "";
                }
                if (memFree < 800) {
                    kolor = "§e";
                    powiadom = "";
                }
                if (memFree < 600) {
                    kolor = "§6";
                    powiadom = "";
                }
                if (memFree < 400) {
                    kolor = "§c";
                    powiadom = "";
                }
                if (memFree < 200) {
                    kolor = "§4";
                    powiadom = "";
                }
                if (memFree < 100) {
                    kolor = "§4";
                    powiadom = "§c!";
                }
                else if (memFree >= 1000){
                    kolor = "§2";
                    powiadom = "";
                }
                Score ram = objective.getScore("§e» " + kolor + "§lRAM §e» §6" + memUsed + "§e/§6" + memCala + " " + powiadom);
                ram.setScore(9);
                Score uptime = objective.getScore("§e» §6Aktywny od §e» " + PlaceholderAPI.setPlaceholders(p, "%server_uptime%"));
                uptime.setScore(8);
                Score newest_t = objective.getScore("§e» §6Najnowszy gracz§e:");
                newest_t.setScore(7);
                Score newest = objective.getScore("§e» " + getDataFile().getString("najnowszy"));
                newest.setScore(6);

                p.setScoreboard(admpanel);
                if (getDataFile().getBoolean(p.getName() + ".stream") == true) {
                    //do not set a new scoreboard if there is already the empty one
                    if(!p.getScoreboard().equals(emptyBoard)){
                        p.setScoreboard(emptyBoard);
                    }
                }
            }
    public static void hidePanel(Player p){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard emptyBoard = manager.getNewScoreboard();
        p.setScoreboard(emptyBoard);
    }
}
