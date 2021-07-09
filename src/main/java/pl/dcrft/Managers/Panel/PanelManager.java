package pl.dcrft.Managers.Panel;

import de.myzelyam.api.vanish.VanishAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.LanguageManager;
import pl.dcrft.Utils.RoundUtil;


public class PanelManager {

    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();
    public static String title;

    public static void sendPanel(Player p, PanelType type){

        if (type == PanelType.MOD) {
            title = LanguageManager.getMessage("prefix") + " " + LanguageManager.getMessage("staffpanel.modpanel.title");
        } else {
            title = LanguageManager.getMessage("prefix") + " " + LanguageManager.getMessage("staffpanel.adminpanel.title");
        }
        String prefix = LanguageManager.getMessage("staffpanel.prefix");
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard admpanel = manager.getNewScoreboard();
        Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

        Scoreboard emptyBoard = manager.getNewScoreboard();
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(title);
        p.setScoreboard(emptyBoard);
        Score nick = objective.getScore(LanguageManager.getMessage("staffpanel.nickname_prefix") + p.getName());
        nick.setScore(16);
        Score czat;
        if (ConfigManager.getDataFile().getBoolean("players." + p.getName() + ".adminchat")) {
            if (ConfigManager.getDataFile().getBoolean("players." + p.getName() + ".modchat")) {
                czat = objective.getScore(LanguageManager.getMessage("staffpanel.chat") + " " + LanguageManager.getMessage("staffchat.both"));
            } else {
                czat = objective.getScore(LanguageManager.getMessage("staffpanel.chat") + " "  + LanguageManager.getMessage("staffchat.adminchat.title"));
            }
        } else if (ConfigManager.getDataFile().getBoolean("players." + p.getName() + ".modchat")) {
            czat = objective.getScore(LanguageManager.getMessage("staffpanel.chat") + " " + LanguageManager.getMessage("staffchat.modchat.title"));
        } else {
            czat = objective.getScore(LanguageManager.getMessage("staffpanel.chat") + " " + LanguageManager.getMessage("staffchat.public"));
        }
        czat.setScore(15);
        Score gracze = objective.getScore(LanguageManager.getMessage("staffpanel.players") + " " + (Bukkit.getServer().getOnlinePlayers().size() - VanishAPI.getInvisiblePlayers().size()) + " §7[§f+" + VanishAPI.getInvisiblePlayers().size() + "§7]");
        gracze.setScore(14);
        Score ping = objective.getScore(LanguageManager.getMessage("staffpanel.ping") + " " + p.spigot().getPing() + "ms");
        ping.setScore(13);
        double itps;
        itps = Math.round(Bukkit.getTPS()[0] * 100.0) / 100.0;
        String wyd = LanguageManager.getMessage("staffpanel.performance.tps.unknown");
        if (itps <= 8) {
            wyd = LanguageManager.getMessage("staffpanel.performance.tps.0-10");
        } else if (itps >= 8) {
            wyd = LanguageManager.getMessage("staffpanel.performance.tps.0-10");
            if (itps >= 10) {
                wyd = LanguageManager.getMessage("staffpanel.performance.tps.10-12");
                if (itps >= 14) {
                    wyd = LanguageManager.getMessage("staffpanel.performance.tps.12-14");
                    if (itps >= 16) {
                        wyd = LanguageManager.getMessage("staffpanel.performance.tps.16-18");
                        if (itps >= 18) {
                            wyd = LanguageManager.getMessage("staffpanel.performance.tps.18-19");
                            if (itps >= 19) {
                                wyd = LanguageManager.getMessage("staffpanel.performance.tps.19-20");
                                if (itps >= 20) {
                                    wyd = LanguageManager.getMessage("staffpanel.performance.tps.20");
                                }
                            }
                        }
                    }
                }
            }
        }
        Score wydajnosc = objective.getScore(LanguageManager.getMessage("staffpanel.performance.title") + " " + wyd);
        wydajnosc.setScore(12);
        Score tps = objective.getScore(LanguageManager.getMessage("staffpanel.performance.tps.title") + " " + itps);
        tps.setScore(11);
        String kolor2 = "§2";
        float ms = (float) Bukkit.getAverageTickTime();
        if (ms <= 45) {
            kolor2 = "§2";
        }if (ms >= 45) {
            kolor2 = "§a";
        }if (ms >= 50) {
            kolor2 = "§e";
        }if (ms >= 55) {
            kolor2 = "§6";
        }if (ms >= 60) {
            kolor2 = "§c";
        }if (ms >= 70) {
            kolor2 = "§4";
        }
        Score mss = objective.getScore(LanguageManager.getMessage("staffpanel.performance.mspt") + " " + kolor2 + RoundUtil.round(ms, 2) + "§ems");
        mss.setScore(10);

        Runtime r = Runtime.getRuntime();
        long memUsed = ((r.totalMemory() / 1048576) - (r.freeMemory() / 1048576));
        long memCala = (r.totalMemory() / 1048576);
        long memFree = (r.freeMemory() / 1048576);

        String kolor = "§a";
        String powiadom = "";
        if (memFree < 1000) {
            kolor = "§a";
        }if (memFree < 800) {
            kolor = "§e";
        }if (memFree < 600) {
            kolor = "§6";
        }if (memFree < 400) {
            kolor = "§c";
        }if (memFree < 200) {
            kolor = "§4";
        }if (memFree < 100) {
            kolor = "§4";
            powiadom = "§c!";
        }else if (memFree >= 1000) {
            kolor = "§2";
        }
        Score ram = objective.getScore(prefix + kolor + " " + LanguageManager.getMessage("staffpanel.performance.ram.title") + "" + memUsed + LanguageManager.getMessage("staffpanel.performance.ram.spacer") + memCala + " " + powiadom);
        ram.setScore(9);
        Score uptime = objective.getScore(prefix + " " + LanguageManager.getMessage("staffpanel.performance.uptime") +  " " + PlaceholderAPI.setPlaceholders(p, "%server_uptime%"));
        uptime.setScore(8);
        Score newest_t = objective.getScore(prefix + " " + LanguageManager.getMessage("staffpanel.newest_player"));
        newest_t.setScore(7);
        Score newest = objective.getScore(prefix + " "+ LanguageManager.getMessage("staffpanel.no_newest"));
        if (ConfigManager.getDataFile().getString("najnowszy") != null){
            newest = objective.getScore(prefix + " " + ConfigManager.getDataFile().getString("najnowszy"));
        }
        newest.setScore(6);
        p.setScoreboard(admpanel);
        if (ConfigManager.getDataFile().getBoolean("players." + p.getName() + ".stream")) {
            //do not set a new scoreboard if there is already the empty one
            if (!p.getScoreboard().equals(emptyBoard)) {
                p.setScoreboard(emptyBoard);
            }
        }
    }

    public static void showRepeatingPanel(Player p, PanelType type) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                sendPanel(p, type);
            }
        }, 0, 100);
    }

    public static void updatePanel(Player p, PanelType type) {
        sendPanel(p, type);
    }

    public static void hidePanel(Player p) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard emptyBoard = manager.getNewScoreboard();
        p.setScoreboard(emptyBoard);
    }

    public static void updatePanels(){
        for(Player o : Bukkit.getOnlinePlayers()) {
            if (o.hasPermission("panel.adm")) {
                showRepeatingPanel(o, PanelType.ADMIN);
            }
            else if (o.hasPermission("panel.mod")) {
                showRepeatingPanel(o, PanelType.MOD);
            }
        }
    }
}
