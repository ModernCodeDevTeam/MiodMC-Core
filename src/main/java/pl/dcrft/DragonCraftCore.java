package pl.dcrft;

import de.myzelyam.api.vanish.VanishAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import pl.dcbot.main.Bot;
import pl.dcrft.Managers.CommandManager;
import pl.dcrft.Managers.SessionManager;
import pl.dcrft.Managers.DataManager;
import pl.dcrft.Utils.Error.ErrorReason;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static pl.dcrft.Managers.ConfigManger.*;
import static pl.dcrft.Managers.ConnectionManager.*;
import static pl.dcrft.Managers.DataManager.saveData;
import static pl.dcrft.Utils.ConfigUtil.initializeFiles;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;
import static pl.dcrft.Utils.RoundUtil.round;

public class DragonCraftCore extends JavaPlugin implements Listener, CommandExecutor {
    public static DragonCraftCore instance;

    private String prefix = getDataFile().getString("prefix");

    public ArrayList<SessionManager> list = new ArrayList<>();

    public static DragonCraftCore getInstance() {
        return instance;
    }

    public Map<String, Object> filtry;
    public DragonCraftCore() {
        this.filtry = new HashMap<>();
    }


    public void onEnable() {
        initializeFiles();
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("§e--------------------------------------------");
        this.getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        this.getLogger().info("§aWłączono wersję §2" + this.getDescription().getVersion());
        this.getLogger().info("§e--------------------------------------------");

        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                } catch (SQLException | ClassNotFoundException e) {
                    logError(ErrorReason.DATABASE);
                    e.printStackTrace();
                }

            }
        };
        runnable.runTaskAsynchronously(this);
        getRunnable().runTaskTimer(this, 0L, 1200L);

        List<Command> commands = PluginCommandYamlParser.parse(this);
        for (int i=0; i<commands.size(); i++){
            getCommand(commands.get(i).getName()).setExecutor(new CommandManager());
        }

        this.filtry = this.getConfig().getConfigurationSection("filtry").getValues(true);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                final Random rand = new Random();
                final String randomElement = DragonCraftCore.this.getConfig().getStringList("wiadomosci").get(rand.nextInt(DragonCraftCore.this.getConfig().getStringList("wiadomosci").size()));
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', DragonCraftCore.this.getConfig().getString("przedrostekwiadomosci")) + ChatColor.translateAlternateColorCodes('&', randomElement));
            }
        }, 20L, Integer.parseInt(this.getConfig().getString("cooldown")) * 20);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p.hasPermission("panel.mod")) {
                        ScoreboardManager manager = Bukkit.getScoreboardManager();
                        Scoreboard admpanel = manager.getNewScoreboard();
                        Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                        Scoreboard emptyBoard = manager.getNewScoreboard();
                        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        if (!p.hasPermission("panel.adm")) {
                            objective.setDisplayName(prefix + "§a§lMod§2§lPanel");
                        }
                        if (p.hasPermission("panel.adm")) {
                            objective.setDisplayName(prefix + "§c§lAdmin§4§lPanel");
                        }
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
                        Score zgl = objective.getScore("§e» §6Zgłoszenie §e» " + Bot.getInstance().getConfig().getString("numer"));
                        zgl.setScore(5);
                        Score zgl_dsc = objective.getScore("§e» §6Zgłoszenie (DSC) §e» " + Bot.getInstance().getConfig().getString("numerek_dsc"));
                        zgl_dsc.setScore(4);

                        p.setScoreboard(admpanel);
                        if (getDataFile().getBoolean(p.getName() + ".stream") == true) {
                            p.setScoreboard(emptyBoard);
                        }
                    }
                }
            }
        }, 0L, 100L);
    }







    public void onDisable() {
        getLogger().info("§e--------------------------------------------");
        getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        getLogger().info("§cWyłączono wersję §2" + this.getDescription().getVersion());
        getLogger().info("§e--------------------------------------------");
        closeConnection();

    }



    public static boolean isPlayerInGroup(final Player player, final String group) {
        return player.hasPermission("group." + group);
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        if(!getDataFile().getBoolean("czat") && !e.getPlayer().hasPermission("panel.mod")){
            e.setCancelled(true);
            e.getPlayer().sendMessage(prefix + "§cCzat jest wyciszony.");
            return;
        }
        final Player p = e.getPlayer();
        final boolean grupaczer = isPlayerInGroup(p, this.getConfig().getString("czerwonyczat"));
        final boolean grupaczer2 = isPlayerInGroup(p, this.getConfig().getString("czerwonyczat2"));
        final boolean grupaziel = isPlayerInGroup(p, this.getConfig().getString("zielonyczat"));
        final boolean grupaziel2 = isPlayerInGroup(p, this.getConfig().getString("zielonyczat2"));
        final boolean grupaziel3 = isPlayerInGroup(p, this.getConfig().getString("zielonyczat3"));
        if (grupaczer) {
            final String wiadomosc = e.getMessage();
            e.setMessage("§c" + wiadomosc);
        }
        if (grupaczer2) {
            final String wiadomosc = e.getMessage();
            e.setMessage("§c" + wiadomosc);
        }
        if (grupaziel) {
            final String wiadomosc = e.getMessage();
            e.setMessage("§a" + wiadomosc);
        }
        if (grupaziel2) {
            final String wiadomosc = e.getMessage();
            e.setMessage("§a" + wiadomosc);
        }
        if (grupaziel3) {
            final String wiadomosc = e.getMessage();
            e.setMessage("§a" + wiadomosc);
        }
        String message = e.getMessage();
        String niezmieniona = e.getMessage();
        for (final Map.Entry<String, Object> filter : this.filtry.entrySet()) {
            message = message.toLowerCase().replaceAll(filter.getKey().toLowerCase(), filter.getValue().toString());
        }
        if (!message.equalsIgnoreCase(niezmieniona)) {
            e.setMessage(message);

            //String[] wartosci = getConfig().getStringList("zastepowanie").;
            //Boolean foundAtLeastOne = false;
            //for (String word : wartosci) {
            //    if (message.indexOf(word) > 0) {
            //        foundAtLeastOne = true;
            //        break;
            //    }
            //}
            String[] words = getConfig().getStringList("zastepowanie").toArray(new String[0]);

            String finalMessage = message;
            if (!Stream.of(words).anyMatch(word -> finalMessage.contains(word.toLowerCase()))) {
                getServer().getLogger().info(prefix + "§e" + e.getPlayer().getName() + " §cużył niedozwolonego słowa §e» §e" + niezmieniona);

                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("panel.mod") && getDataFile().getBoolean(o.getName() + ".stream") == false) {
                        o.sendMessage(prefix + "§e" + e.getPlayer().getName() + " §cużył niedozwolonego słowa §e» §e" + niezmieniona);
                    }
                }
            }

            //if (message.contains((CharSequence) getConfig().getStringList("zastepowanie"))) {
            //    }


        }
        if (e.getMessage().length() == 0) {
            e.setCancelled(true);
        }
        if (getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat")) {
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat") == true) {
                if (getDataFile().getBoolean(e.getPlayer().getName() + ".stream") == true) {
                    e.getPlayer().sendMessage(prefix + "§cPodczas trybu §estreamowania§c nie można pisać na tym czacie. Wyłączono czat automatycznie.");
                    getDataFile().set(e.getPlayer().getName() + ".adminchat", false);
                    e.setCancelled(true);
                    saveData();
                    return;
                }
                e.setCancelled(true);
                Player sender = e.getPlayer();
                String wiad;
                if (!message.equalsIgnoreCase(niezmieniona)) {
                    wiad = message;
                }
                else {
                    wiad = niezmieniona;
                }
                getServer().getLogger().info("§c§lAdmin§4§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §c" + wiad);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("admin.see") && getDataFile().getBoolean(o.getName() + ".stream") == false) {
                        o.sendMessage("§c§lAdmin§4§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §c" + wiad);
                    }
                }
            }
        }
        if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat")) {
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat") == true) {
                if (getDataFile().getBoolean(e.getPlayer().getName() + ".stream") == true) {
                    e.getPlayer().sendMessage(prefix + "§cPodczas trybu §estreamowania§c nie można pisać na tym czacie. Wyłączono czat automatycznie.");
                    getDataFile().set(e.getPlayer().getName() + ".modchat", false);
                    e.setCancelled(true);
                    saveData();
                    return;
                }
                e.setCancelled(true);
                Player sender = e.getPlayer();
                String wiad;
                if (!message.equalsIgnoreCase(niezmieniona)) {
                    wiad = message;
                }
                else {
                    wiad = niezmieniona;
                }
                getServer().getLogger().info("§a§lMod§2§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §c" + wiad);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("mod.see") && getDataFile().getBoolean(o.getName() + ".stream") == false) {
                        o.sendMessage("§a§lMod§2§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §a" + wiad);

                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException, ClassNotFoundException {
        SessionManager newSession = new SessionManager(event.getPlayer());
        this.list.add(newSession);
        //TODO informacja o wersji tylko jesli jest inna niz zalecana
        @NotNull List<Integer> sver = getConfig().getIntegerList("serwer.wersje");
        int pver = event.getPlayer().getProtocolVersion();
        if(!sver.contains(pver)){
            event.getPlayer().sendMessage(prefix + "§cJeśli masz wersję inną niż §e1.16-1.16.5§c, mogą wystąpić pewne błędy w rozgrywce. Prosimy ich nie zgłaszać. Serwer przystosowany jest do działania na wersjach §e1.16-1.16.5§c. Używanie innych wersji może skutkować błędami, w razie ich wystąpienia zmień wersję na §e1.16-1.16.5§c.");
        }

        if(!event.getPlayer().hasPlayedBefore()) {
            getDataFile().set("najnowszy", event.getPlayer().getName());
            saveData();
        }
        if (event.getPlayer().hasPermission("mod.chat")) {
            if(!getDataFile().contains(event.getPlayer().getName())){
                getDataFile().set(event.getPlayer().getName() + ":", null);
                getDataFile().set(event.getPlayer().getName() + ".modchat" , false);
                getDataFile().set(event.getPlayer().getName() + ".adminchat" , false);
                saveData();
            }
            if (getDataFile().getBoolean(event.getPlayer().getName() + ".modchat") == true) {
                getDataFile().set(event.getPlayer().getName() + ".modchat", false);
                saveData();
                return;
            }
        }
        if (event.getPlayer().hasPermission("admin.chat")) {
            if(!getDataFile().contains(event.getPlayer().getName())){
                getDataFile().set(event.getPlayer().getName() + ":", null);
                getDataFile().set(event.getPlayer().getName() + ".modchat" , false);
                getDataFile().set(event.getPlayer().getName() + ".adminchat" , false);
                saveData();
            }
            if (getDataFile().getBoolean(event.getPlayer().getName() + ".adminchat") == false) {
                getDataFile().set(event.getPlayer().getName() + ".adminchat", true);
                event.getPlayer().sendMessage("§c§lAdmin§4§lChat §e» §aAutomatycznie włączono czat.");
                saveData();
                return;
            }
        }
        if (!event.getPlayer().hasPermission("pt.adm")) {
            getDataFile().set(event.getPlayer().getName() + ".online", null);
            saveData();
            BukkitRunnable runnable = new BukkitRunnable() {
                public void run() {
                    try {
                        openConnection();
                        Statement statement = connection.createStatement();
                        statement.executeUpdate("UPDATE staty_ogolem SET online='teraz', serwer_online='" + getConfig().getString("nazwa_serwera") + "' WHERE nick = '" + event.getPlayer().getName() + "'");
                        int kille;
                        int dedy;
                        float kdr;
                        String ranga;
                        String update;
                            kille = Integer.parseInt(PlaceholderAPI.setPlaceholders(event.getPlayer(), "%statistic_player_kills%"));
                            dedy = Integer.parseInt(PlaceholderAPI.setPlaceholders(event.getPlayer(), "%statistic_deaths%"));
                            if (dedy == 0) {
                                kdr = (float)kille;
                            } else if (kille == 0) {
                                kdr = 0.0F;
                            } else {
                                kdr = (float)kille / (float)dedy;
                            }

                            kdr = round(kdr, 2);
                            ranga = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%vault_rank%");
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

                            update = PlaceholderAPI.setPlaceholders(event.getPlayer(), "UPDATE `" + tabela + "` SET kille = '%statistic_player_kills%', dedy = '%statistic_deaths%', kdr = '" + kdr + "', ranga = '" + ranga + "', bloki = '%statistic_mine_block%' WHERE nick = '" + event.getPlayer().getName() + "'");
                            statement.executeUpdate(update);

                        statement.close();
                    } catch (SQLException | ClassNotFoundException var9) {
                        DragonCraftCore.getInstance().getLogger().info("§e--------------------------------------------");
                        DragonCraftCore.getInstance().getLogger().info("§cWystąpił problem podczas łączenia się z bazą danych! Wszelke przydatne informacje znajdziesz poniżej.");
                        DragonCraftCore.getInstance().getLogger().info("§e--------------------------------------------");
                        var9.printStackTrace();
                    }

                }
            };
            runnable.runTaskAsynchronously(this);
        }
    }
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) throws SQLException, ClassNotFoundException {
        if (!event.getPlayer().hasPermission("pt.adm")) {
            Player p = event.getPlayer();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.YYYY 'o' HH:mm");
            LocalDateTime now = LocalDateTime.now();
            getDataFile().set(p.getName() + ".online", dtf.format(now));
            saveData();
            BukkitRunnable runnable = new BukkitRunnable() {
                public void run() {
                    try {
                        openConnection();
                        Statement statement = connection.createStatement();
                        String update = PlaceholderAPI.setPlaceholders(event.getPlayer(), "UPDATE staty_ogolem SET online='"+ dtf.format(now) + "', serwer_online='null' WHERE nick = '" + event.getPlayer().getName() + "'");
                        statement.executeUpdate(update);
                        statement.close();
                    } catch (SQLException | ClassNotFoundException var5) {
                        DragonCraftCore.getInstance().getLogger().info("§e--------------------------------------------");
                        DragonCraftCore.getInstance().getLogger().info("§cWystąpił problem podczas łączenia się z bazą danych! Wszelke przydatne informacje znajdziesz poniżej.");
                        DragonCraftCore.getInstance().getLogger().info("§e--------------------------------------------");
                        var5.printStackTrace();
                    }

                }
            };
            runnable.runTaskAsynchronously(this);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (title.contains("§6Profil §e» §3")) {
            e.setCancelled(true);
        }

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == Result.KICK_FULL) {
            Player player = event.getPlayer();
            if (player != null) {
            }

            if (player != null && player.hasPermission("vipslot.allow")) {
                event.allow();
                return;
            } else {
                event.setKickMessage(prefix + "§cSerwer jest pełen graczy!\n§cZakup rangę §b§lVIP§c, aby dołączyć na serwer, gdy jest pełen!");
            }
        }

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/warn")) {
            e.setCancelled(true);
            String[] args = e.getMessage().split(" ");
            if(!e.getPlayer().hasPermission("ab.warn.temp")) {
                e.getPlayer().sendMessage(prefix + "§cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                return;
            }
            else if (args.length < 3) {
                e.getPlayer().sendMessage("§e§lDragon§6§lCraft§e » §cUżycie: §e/warn <nick> <powód> [-s]§c.");
            }
            else {
                Player p = e.getPlayer();
                final StringBuilder sb = new StringBuilder();
                for (int k = 2; k < args.length; ++k) {
                    sb.append(args[k]).append(" ");
                }
                final String allArgs = sb.toString().trim();
                p.chat("/tempwarn " + args[1] + " #1 " + allArgs);
            }
        }
    }
    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent e){
        Player p = e.getPlayer();
        for (int i = 0; i < list.size(); i++) {
            if (p.getUniqueId() == list.get(i).getPlayer().getUniqueId()) {
                list.get(i).resetMinute();
                break;
            }
        }
        if(e.getClickedBlock() != null && e.getClickedBlock().getType() != null && e.getClickedBlock().getType().isBlock() && e.getClickedBlock().getType() == Material.LEVER) {
            if(Integer.parseInt(getConfig().getString("cooldown_lever")) > 0){
                e.setCancelled(true);
                p.sendMessage(prefix + "§cPoczekaj sekundę przed użyciem tej dźwigni.");
                return;
            }
            else {
                getDataFile().set("cooldown_lever", 1);
                saveData();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        getDataFile().set("cooldown_lever", 0);
                        saveData();
                    }
                }, 10L);
                return;
            }
        }
        return;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for (int i = 0; i < this.list.size(); i++) {
            SessionManager session = this.list.get(i);
            if (session.getPlayer().getName().equalsIgnoreCase(p.getName()))
                this.list.remove(session);
        }
    }
    public BukkitRunnable getRunnable() {
        return new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < DragonCraftCore.this.list.size(); i++) {
                    SessionManager session = DragonCraftCore.this.list.get(i);
                    session.increaseMinute();
                }

            }
        };
    }
}
