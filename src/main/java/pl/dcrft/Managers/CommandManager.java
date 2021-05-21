package pl.dcrft.Managers;

import com.fasterxml.jackson.databind.ext.Java7Support;
import de.myzelyam.api.vanish.VanishAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import pl.dcbot.main.Bot;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static pl.dcrft.DragonCraftCore.*;
import static pl.dcrft.Managers.ConfigManger.getDataFile;
import static pl.dcrft.Managers.ConnectionManager.*;
import static pl.dcrft.Managers.DataManager.saveData;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;
import static pl.dcrft.Utils.RoundUtil.round;


public class CommandManager implements CommandExecutor {
    private static DragonCraftCore plugin;

    public ArrayList<SessionManager> list = new ArrayList<>();

    private String prefix = getDataFile().getString("prefix");

    @SuppressWarnings({ "unchecked", "unused", "rawtypes" })
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("nieafk")) {
            Player p = (Player) sender;
            boolean sond_on_notafk = Boolean.parseBoolean(plugin.getConfig().getString("afk.sond_on_notafk"));
            if (sond_on_notafk == true) {
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 3);
            } else {

            }
            for (int i = 0; i < list.size(); i++) {
                if (p.getUniqueId() == list.get(i).getPlayer().getUniqueId()) {
                    list.get(i).resetMinute();
                    break;
                }
            }
            p.sendMessage("§7" + plugin.getConfig().getString("afk.prefix") + " " + plugin.getConfig().getString("afk.kick_not_afk_msg"));

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("z") || cmd.getName().equalsIgnoreCase("znajomi") || cmd.getName().equalsIgnoreCase("f")) {
            if (args.length == 0) {
                sender.sendMessage(prefix + " §6Znajomi");
                sender.sendMessage("§e» §6/znajomi lista §e» §3lista znajomych");
                sender.sendMessage("§e» §6/znajomi dodaj §enick » §3dodaj gracza do znajomych");
                sender.sendMessage("§e» §6/znajomi usun §enick » §3usuń gracza ze znajomych");
                sender.sendMessage("§e» §6/znajomi akceptuj §enick » §3zaakceptuj zaproszenie do znajomych od gracza");
                sender.sendMessage("§e» §6/znajomi odrzuc §enick » §3odrzuć zaproszenie do znajomych od gracza");
                return false;
            }
            if (args[0].equalsIgnoreCase("lista") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("info")) {
                sender.sendMessage(prefix + " §6Lista znajomych");
                List<String> znajomi = getDataFile().getStringList(sender.getName() + ".znajomi");
                if (znajomi.size() == 0) {
                    sender.sendMessage("§e» §cbrak");
                    return false;
                } else {
                    for (int i = 0; i < znajomi.size(); i++) {
                        String online = getDataFile().getString(znajomi.get(i) + ".online");
                        if (online == null) {
                            online = "§aonline";
                        }
                        sender.sendMessage("§e» §3" + znajomi.get(i) + " §e» §c" + online);
                    }
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("usun") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("u") || args[0].equalsIgnoreCase("wyrzuc")) {
                if (args.length == 1) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza.");
                    return false;
                } else {
                    List<String> znajomip = getDataFile().getStringList(sender.getName() + ".znajomi");
                    List<String> znajomio = getDataFile().getStringList(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi");
                    if (!znajomip.contains(args[1])) {
                        sender.sendMessage(prefix + " §e" + Bukkit.getOfflinePlayer(args[1]).getName() + "§c nie jest twoim znajomym.");
                        return false;
                    } else {
                        znajomip.remove(Bukkit.getOfflinePlayer(args[1]).getName());
                        znajomio.remove(sender.getName());
                        getDataFile().set(sender.getName().toLowerCase() + ".znajomi", znajomip);
                        getDataFile().set(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi", znajomio);
                        saveData();
                        sender.sendMessage(prefix + " §e" + Bukkit.getOfflinePlayer(args[1]).getName() + "§a nie jest już twoim znajomym.");
                        return false;
                    }
                }
            }
            if (args[0].equalsIgnoreCase("dodaj") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("d")) {
                if (args.length == 1) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza.");
                    return false;
                }
                if (args[1].equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(prefix + " §cNo co ty! Siebie samego chcesz dodawać do znajomych? Pff!");
                    return false;
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza online.");
                    return false;
                }
                if (!Bukkit.getPlayer(args[1]).isOnline()) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza online.");
                    return false;
                }
                if (args[1].equalsIgnoreCase("NickNickerYT") || args[1].equalsIgnoreCase("JaneQ") || args[1].equalsIgnoreCase("kalkulator888") || args[1].equalsIgnoreCase("MikiIgi192")) {
                    sender.sendMessage(prefix + " §cNo nie bardzo. Administracja to nie twoi znajomi.");
                    return false;
                } else {
                    List<String> znajomip = getDataFile().getStringList(sender.getName() + ".znajomi");
                    if (znajomip.contains(Bukkit.getOfflinePlayer(args[1]).getName())) {
                        sender.sendMessage(prefix + " §cTen gracz jest już twoim znajomym!");
                        return false;
                    }
                    getDataFile().set(sender.getName() + ".znajprosba." + Bukkit.getOfflinePlayer(args[1]).getName(), true);
                    saveData();
                    sender.sendMessage(prefix + " §aZaproszono §e" + Bukkit.getOfflinePlayer(args[1]).getName() + " §ado znajomych.");
                    Bukkit.getPlayer(args[1]).sendMessage(prefix + " §aOtrzymano zaproszenie do znajomych od §e" + sender.getName() + "§a.");
                    Bukkit.getPlayer(args[1]).sendMessage(prefix + " §aUżyj §e/znajomi akceptuj §e" + sender.getName() + "§a, aby ją zaakceptować.");
                    Bukkit.getPlayer(args[1]).sendMessage(prefix + " §aUżyj §e/znajomi odrzuc §e" + sender.getName() + "§a, aby ją odrzucić.");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("akceptuj") || args[0].equalsIgnoreCase("a")) {
                if (args.length == 1) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza.");
                    return false;
                }
                if (!getDataFile().getBoolean(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName())) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza, który wysłał do ciebie zaproszenie do znajomych");
                    return false;
                } else {
                    getDataFile().set(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName(), null);
                    List<String> znajomip = getDataFile().getStringList(sender.getName() + ".znajomi");
                    List<String> znajomio = getDataFile().getStringList(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi");
                    znajomip.add(Bukkit.getOfflinePlayer(args[1]).getName());
                    znajomio.add(sender.getName());
                    getDataFile().set(sender.getName() + ".znajomi", znajomip);
                    getDataFile().set(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi", znajomio);
                    saveData();
                    if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                        Bukkit.getPlayer(args[1]).sendMessage(prefix + " §e" + sender.getName() + " §azaakceptował twoje zaproszenie do znajomych.");
                    }
                    sender.sendMessage(prefix + " §aZaakceptowano zaprosznie do znajomych od §e" + Bukkit.getOfflinePlayer(args[1]).getName() + "§a.");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("odrzuc") || args[0].equalsIgnoreCase("o")) {
                if (args.length == 1) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza.");
                    return false;
                }
                if (!getDataFile().getBoolean(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName())) {
                    sender.sendMessage(prefix + " §cPodaj nick gracza, który wysłał do ciebie zaproszenie do znajomych");
                    return false;
                } else {
                    getDataFile().set(Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName(), null);
                    saveData();
                    if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                        Bukkit.getPlayer(args[1]).sendMessage(prefix + " §e" + sender.getName() + " §codrzucił twoje zaproszenie do znajomych.");
                    }
                    sender.sendMessage(prefix + " §cOdrzucono zaprosznie do znajomych od §e" + Bukkit.getOfflinePlayer(args[1]).getName() + "§a.");
                    return false;
                }
            } else {
                sender.sendMessage(prefix + " §6Znajomi");
                sender.sendMessage("§e» §6/znajomi lista §e» §3lista znajomych");
                sender.sendMessage("§e» §6/znajomi dodaj §enick » §3dodaj gracza do znajomych");
                sender.sendMessage("§e» §6/znajomi usun §enick » §3usuń gracza ze znajomych");
                sender.sendMessage("§e» §6/znajomi akceptuj §enick » §3zaakceptuj zaproszenie do znajomych od gracza");
                sender.sendMessage("§e» §6/znajomi odrzuc §enick » §3odrzuć zaproszenie do znajomych od gracza");
                return false;
            }
        }


        if (cmd.getName().equalsIgnoreCase("slub")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(prefix + " §cPodaj nick gracza.");
                return false;
            }
            if (!Bukkit.getPlayer(args[0]).isOnline()) {
                p.sendMessage(prefix + " §cPodaj poprawny nick gracza.");
                return false;
            }
            Player other = Bukkit.getPlayer(args[0]);
            if (args[0].equalsIgnoreCase("JaneQ") || args[0].equalsIgnoreCase("NickNickerYT") || args[0].equalsIgnoreCase("MikiIgi192") || args[0].equalsIgnoreCase("kalkulator888")) {
                sender.sendMessage(prefix + " §cPodaj poprawny nick gracza.");
                return false;
            }
            if (args[0].equalsIgnoreCase(p.getName())) {
                p.sendMessage(prefix + " §cNo coś ty, chcesz się związać z samym sobą?");
                return false;
            } else {
                if (getDataFile().getString(p.getName() + ".slub") != null) {
                    p.sendMessage(prefix + " §cJuż jesteś w związku.");
                    return false;
                }
                if (getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slub") != null) {
                    p.sendMessage(prefix + " §cTen gracz już jest w związku.");
                    return false;
                } else {
                    getDataFile().set(p.getName() + ".slubprosba", Bukkit.getOfflinePlayer(args[0]).getName());
                    saveData();
                    p.sendMessage(prefix + " §aWysłano prośbę o ślub.");
                    other.sendMessage(prefix + " §e" + p.getName() + " §aprosi cię o ślub! Wpisz §e/sakceptuj " + p.getName() + " §alub §e/sodrzuc " + p.getName() + "§a, aby odpowiedzieć na prośbę.");
                    return true;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("sodrzuc")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(prefix + " §cPodaj nick gracza.");
                return false;
            }
            if (getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                p.sendMessage(prefix + " §cTen gracz nie wysłał do ciebie prośby o ślub.");
                return false;
            } else {
                p.sendMessage(prefix + " §aProśba o ślub została odrzucona.");
                if (Bukkit.getPlayer(args[0]).isOnline()) {
                    Bukkit.getPlayer(args[0]).sendMessage(prefix + " §aProśba o ślub została odrzucona.");
                }
                getDataFile().set(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                saveData();
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("sakceptuj")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(prefix + " §cPodaj nick gracza.");
                return false;
            }
            if (getDataFile().getString(p.getName() + ".slub") != null) {
                p.sendMessage(prefix + " §cJesteś już w związku.");
                return false;
            }
            if (getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                p.sendMessage(prefix + " §cTen gracz nie wysłał do ciebie prośby o ślub.");
                return false;
            } else {
                if (getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + "slub") != null) {
                    p.sendMessage(prefix + " §cTen gracz jest już w związku.");
                    return false;
                }
                if (!p.getInventory().contains(Material.DIAMOND)) {
                    p.sendMessage(prefix + " §cNie posiadasz 1 diamentu potrzebnego do zawarcia ślubu.");
                    return false;
                } else {
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                    if (Bukkit.getPlayer(args[0]).isOnline()) {
                        Bukkit.getPlayer(args[0]).sendMessage(prefix + " §aŚlub został zawarty!");
                    }
                    p.sendMessage(prefix + " §aŚlub został zawarty!");
                    getDataFile().set(p.getName() + ".slub", Bukkit.getOfflinePlayer(args[0]).getName());
                    getDataFile().set(Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", p.getName());
                    getDataFile().set(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                    getDataFile().set(p.getName() + ".slubprosba", null);
                    saveData();
                    BukkitRunnable runnable = new BukkitRunnable() {
                        public void run() {
                            try {
                                openConnection();
                                Statement statement = connection.createStatement();
                                String updatep = "UPDATE `" + tabela + "` SET slub = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "' WHERE nick = '" + p.getName() + "'";
                                String updateo = "UPDATE `" + tabela + "` SET slub = '" + p.getName() + "' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                                statement.executeUpdate(updatep);
                                statement.executeUpdate(updateo);
                                statement.close();
                            } catch (SQLException | ClassNotFoundException var5) {
                                logError(ErrorReason.DATABASE);
                                var5.printStackTrace();
                            }

                        }
                    };
                    runnable.runTaskAsynchronously(plugin);
                    plugin.getServer().broadcastMessage(prefix + " §e" + p.getName() + " §bi §e" + Bukkit.getOfflinePlayer(args[0]).getName() + " §bwłaśnie zawarli ślub!");
                    return false;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("rozwod")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(prefix + " §cPodaj nick gracza.");
                return false;
            }
            if (args[0].equalsIgnoreCase(p.getName())) {
                p.sendMessage(prefix + " §cNo... Nie.");
                return false;
            }
            if (getDataFile().getString(p.getName() + ".slub") == null) {
                p.sendMessage(prefix + " §cNie jesteś w związku.");
                return false;
            }
            if (getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slub") == null || !getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slub").equalsIgnoreCase(p.getName()) || !getDataFile().getString(p.getName() + ".slub").equalsIgnoreCase(Bukkit.getOfflinePlayer(args[0]).getName())) {
                p.sendMessage(prefix + " §cNie jesteś w związku z tą osobą.");
                return false;
            } else {
                plugin.getServer().broadcastMessage(prefix + " §e" + p.getName() + " §bi §e" + Bukkit.getOfflinePlayer(args[0]).getName() + " §bwłaśnie się rozwiedli.");
                getDataFile().set(p.getName() + ".slub", null);
                getDataFile().set(Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", null);
                getDataFile().set(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                getDataFile().set(p.getName() + ".slubprosba", null);
                saveData();
                BukkitRunnable runnable = new BukkitRunnable() {
                    public void run() {
                        try {
                            openConnection();
                            Statement statement = connection.createStatement();
                            String updatep = "UPDATE `" + tabela + "` SET slub = 'NULL' WHERE nick = '" + p.getName() + "'";
                            String updateo = "UPDATE `" + tabela + "` SET slub = 'NULL' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                            statement.executeUpdate(updatep);
                            statement.executeUpdate(updateo);
                            statement.close();
                        } catch (SQLException | ClassNotFoundException var5) {
                            logError(ErrorReason.DATABASE);
                            var5.printStackTrace();
                        }

                    }
                };
                runnable.runTaskAsynchronously(plugin);
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("czat")) {
            if (!sender.hasPermission("panel.mod")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                return false;
            } else {
                if (getDataFile().getBoolean("czat") == true) {
                    getDataFile().set("czat", false);
                    saveData();
                    plugin.getServer().broadcastMessage(prefix + " §cCzat został wyciszony.");
                    return true;
                } else {
                    getDataFile().set("czat", true);
                    saveData();
                    plugin.getServer().broadcastMessage(prefix + " §aCzat został włączony.");
                    return true;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("pomoc")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("przedrostek")));
            for (final String msg : plugin.getConfig().getStringList("pomoc")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("vip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, "vip");
            if (grupa) {
                p.chat("/warp vip");
                return true;
            }
            for (final String msg2 : plugin.getConfig().getStringList("vip")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg2));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("svip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, "svip");
            if (grupa) {
                p.chat("/warp svip");
                return true;
            }
            for (final String msg2 : plugin.getConfig().getStringList("svip")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg2));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("mvip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, "mvip");
            if (grupa) {
                p.chat("/warp mvip");
                return true;
            }
            for (final String msg2 : plugin.getConfig().getStringList("mvip")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg2));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("evip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, "evip");
            if (grupa) {
                p.chat("/warp evip");
                return true;
            }
            for (final String msg2 : plugin.getConfig().getStringList("evip")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg2));
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("cc")) {
            if (sender.hasPermission("cc.adm")) {
                for (int i = 0; i < 100; ++i) {
                    Bukkit.getServer().broadcastMessage("");
                }
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cc")));
                return true;
            }
            if (!sender.hasPermission("cc.adm")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("dcccast") && args.length != 0) {
            if (sender.hasPermission("dcc.adm")) {
                final StringBuilder sb = new StringBuilder();
                for (int j = 0; j < args.length; ++j) {
                    sb.append(args[j]).append(" ");
                }
                final String allArgs = sb.toString().trim();
                Bukkit.getServer().broadcastMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&', allArgs));
                return true;
            }
            if (!sender.hasPermission("cc.adm")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("dcc")) {
            if (args.length == 0) {
                if (sender.hasPermission("dcc.adm")) {
                    sender.sendMessage("§e§lDragon§6§lCraft§a§lCore " + plugin.getDescription().getVersion());
                    sender.sendMessage("§e» §6/dcc przeladuj§3 - prze\u0142adowuje config");
                    sender.sendMessage("§e» §6/dcc afk§3 - wyświetla konfigurację wyrzucania za AFK");
                    sender.sendMessage("§e» §6/dcccast§3 - wyświetla ogłoszenie na publicznym czacie");
                    sender.sendMessage("§e» §6/pomoc§3 - wy\u015bwietla pomoc");
                    sender.sendMessage("§e» §6/sklepbroadcast§3 - ogłasza zakup ze sklepu");
                    sender.sendMessage("§e» §6/sklepbroadcastdonate§3 - ogłasza donate");
                    sender.sendMessage("§e» §6/cc§3 - czy\u015bci czat");
                    sender.sendMessage("§e» §6/losuj§3 - losuje");
                    sender.sendMessage("§e» §6/reload60§3 - prze\u0142adowuje serwer za minut\u0119");
                    sender.sendMessage("§e» §6/restart60§3 - restartuje serwer za minut\u0119");
                    sender.sendMessage("§e» §6/stop60§3 - zatrzymuje serwer za minut\u0119");
                    sender.sendMessage("§e» §6/przerwa60§3 - rozpoczyna przerw\u0119 techniczn\u0105 za minut\u0119  ");
                    sender.sendMessage("§e» §6/mc§3 - przełącza §a§lMod§2§lChat");
                    sender.sendMessage("§e» §6/ac§3 - przełącza §c§lAdmin§4§lChat");
                    sender.sendMessage("§e» §6/stream§3 - przełącza tryb streamu");
                    sender.sendMessage("§e» §6/czat§3 - przełącza czat");
                    return true;
                }
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                return true;
            } else {
                if (!sender.hasPermission("dcc.adm")) {
                    sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                    return false;
                }
                final String sub = args[0];
                if (sub.equalsIgnoreCase("przeladuj")) {
                    plugin.reloadConfig();
                    sender.sendMessage(prefix + " §aPrze\u0142adowano plik konfiguracyjny §e§lDragon§6§lCraft§a§lCore§a.");
                    plugin.filtry = plugin.getConfig().getConfigurationSection("filtry").getValues(true);
                    return true;
                }
                if (sub.equalsIgnoreCase("afk")) {
                    String config_values = plugin.getConfig().getString("afk.config_values");
                    String config_reload = plugin.getConfig().getString("afk.config_reload");
                    String prefix = plugin.getConfig().getString("afk.prefix");
                    String kick_msg = plugin.getConfig().getString("afk.kick_msg");
                    Integer kick_warn_delay = plugin.getConfig().getInt("afk.kick_warn_delay");
                    String kick_warn_msg = plugin.getConfig().getString("afk.kick_warn_msg");
                    String kick_warn_msg_afk = plugin.getConfig().getString("afk.kick_warn_msg_afk");
                    Integer kick_delay = plugin.getConfig().getInt("afk.kick_delay");
                    String sound_on_get_warn = plugin.getConfig().getString("afk.sound_on_get_warn");
                    String sound_on_notafk = plugin.getConfig().getString("afk.sond_on_notafk");
                    String notafkmsg = plugin.getConfig().getString("afk.kick_not_afk_msg");
                    sender.sendMessage("§e§lDragon§6§lCraft§a§lCore " + plugin.getDescription().getVersion());
                    sender.sendMessage("§e» §a§lKonfiguracja §2§lAFK");
                    sender.sendMessage("");
                    sender.sendMessage("§e» §3Prefiks §e» " + prefix);
                    sender.sendMessage("§e» §3Wiadomość przy wyrzuceniu §e» " + kick_msg);
                    sender.sendMessage("§e» §3Czas, po jakim pojawi się ostrzeżenie §e» " + kick_warn_delay);
                    sender.sendMessage("§e» §3Treść ostrzeżenia §e» " + kick_warn_msg);
                    sender.sendMessage("§e» §3Wiadomość nie-AFK §e» " + kick_warn_msg_afk);
                    sender.sendMessage("§e» §3Wiadomość potwierdzeia nie-AFK §e» " + notafkmsg);
                    sender.sendMessage("§e» §3Czas, po jakim gracz zostanie wyrzucony §e» " + kick_delay);
                    sender.sendMessage("§e» §3Dźwięk ostrzeżenia §e» " + sound_on_get_warn);
                    sender.sendMessage("§e» §3Dźwięk potwierdzenia nie-AFK §e» " + sound_on_notafk);
                    return true;
                } else {
                    sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                    return false;
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("losuj") && args.length == 0) {
            if (sender.hasPermission("panel.adm")) {
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("losuje")));
                final Random rand = new Random();
                final String randomElement = plugin.getConfig().getStringList("kolorki").get(rand.nextInt(plugin.getConfig().getStringList("kolorki").size()));
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("wylosowano")) + ChatColor.translateAlternateColorCodes('&', randomElement));
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("stop60")) {
            if (sender.hasPermission("r.adm")) {
                Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 minut\u0119 serwer zostanie zatrzymany.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 30 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 600L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 15 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 900L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 10 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1000L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 9 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1020L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 8 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1040L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 7 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1060L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 6 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1080L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 5 sekund serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1100L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 4 sekundy serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1120L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 3 sekundy serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1140L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 2 sekundy serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1160L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 sekund\u0119 serwer zostanie zatrzymany.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1180L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZapisywanie plik\u00f3w \u015bwiata..");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1200L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cTrwa zatrzymywanie serwera...");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1260L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "stop");
                    }
                }, 1280L);
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("reload60")) {
            if (sender.hasPermission("r.adm")) {
                Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 minut\u0119 odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 30 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 600L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 15 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 900L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 10 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1000L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 9 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1020L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 8 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1040L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 7 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1060L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 6 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1080L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 5 sekund odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1100L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 4 sekundy odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1120L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 3 sekundy odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1140L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 2 sekundy odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1160L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 sekund\u0119 odb\u0119dzie si\u0119 ponowne za\u0142adowanie plik\u00f3w serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1180L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZapisywanie plik\u00f3w \u015bwiata...");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1200L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cTrwa ponowne \u0142adowanie plik\u00f3w serwera...");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1260L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "reload confirm");
                    }
                }, 1280L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §aPrze\u0142adowano pliki serwera.");
                    }
                }, 1281L);
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("restart60")) {
            if (sender.hasPermission("r.adm")) {
                Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 minut\u0119 odb\u0119dzie si\u0119 restart serwera.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 30 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 600L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 15 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 900L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 10 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1000L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 9 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1020L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 8 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1040L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 7 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1060L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 6 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1080L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 5 sekund odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1100L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 4 sekundy odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1120L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 3 sekundy odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1140L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 2 sekundy odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1160L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 sekund\u0119 odb\u0119dzie si\u0119 restart serwera.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1180L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZapisywanie plik\u00f3w \u015bwiata...");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1200L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cTrwa restart serwera...");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1260L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
                    }
                }, 1280L);
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("przerwa60")) {
            if (sender.hasPermission("r.adm")) {
                Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 minut\u0119 odb\u0119dzie si\u0119 przerwa techniczna.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist on");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 30 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 600L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 15 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 900L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 10 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1000L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 9 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1020L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 8 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1040L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 7 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1060L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 6 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1080L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 5 sekund odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1100L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 4 sekundy odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1120L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 3 sekundy odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1140L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 2 sekundy odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1160L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZa 1 sekund\u0119 odb\u0119dzie si\u0119 przerwa techniczna.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1180L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cZapisywanie plik\u00f3w \u015bwiata...");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1200L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(prefix + " §cTrwa przerwa techniczna...");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    }
                }, 1260L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist on");
                        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                            if (!p.isOp() && !p.isWhitelisted()) {
                                p.kickPlayer(prefix + " §cTrwa przerwa techniczna...");
                            }
                        }
                    }
                }, 1280L);
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("sklepbroadcast")) {
            if (!sender.hasPermission("r.adm")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                return true;
            }
            final StringBuilder sb = new StringBuilder();
            for (int k = 1; k < args.length; ++k) {
                sb.append(args[k]).append(" ");
            }
            final String allArgs = sb.toString().trim();
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("sklep")));
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &3Gracz &b" + args[0] + "&3 zakupi\u0142 &b" + allArgs + "&3!"));
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &3Dzi\u0119kujemy za wsparcie!"));
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &6Smocze jaja&b dost\u0119pne ju\u017c od &61.23z\u0142&b!"));
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &bSprawd\u017a ofert\u0119 na &6DCRFT.PL"));
            for (final Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 8.0f);
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("ac")) {
            if (!sender.hasPermission("admin.chat")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. Użyj §e/info§c, aby dowiedzieć się więcej o dostępnych komendach.");
                return false;
            } else {
                if (!getDataFile().contains(sender.getName())) {
                    Player p = (Player) sender;
                    getDataFile().set(sender.getName() + ":", null);
                    getDataFile().set(sender.getName() + ".adminchat", true);
                    getDataFile().set(sender.getName() + ".modchat", false);
                    sender.sendMessage("§c§lAdmin§4§lChat §e» §aWłączono czat.");

                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard admpanel = manager.getNewScoreboard();
                    Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    if (!p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §a§lMod§2§lPanel");
                    }
                    if (p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §c§lAdmin§4§lPanel");
                    }
                    p.setScoreboard(emptyBoard);
                    Score nick = objective.getScore("§e» §6" + p.getName());
                    nick.setScore(16);
                    Score czat;
                    if (getDataFile().getString(p.getName() + ".adminchat") == "true") {
                        if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lA§4§lC §ei §a§lM§2§lC");
                        } else {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lAdmin§4§lChat");
                        }
                    } else if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                        czat = objective.getScore("§e» §6Czat §e» " + "§a§lMod§2§lChat");
                    } else {
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
                    } else if (itps >= 8) {
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
                    } else {
                        wyd = "?";
                    }
                    Score wydajnosc = objective.getScore("§e» §6Wydajność §e» " + wyd);
                    wydajnosc.setScore(12);
                    Score tps = objective.getScore("§e» §6TPS §e» " + itps);
                    tps.setScore(11);
                    String kolor2 = "§2";
                    float ms = (float) Bukkit.getAverageTickTime();
                    if (ms <= 45) {
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
                    } else if (memFree >= 1000) {
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

                    saveData();
                    return true;
                }
                if (getDataFile().getBoolean(sender.getName() + ".stream") == true) {
                    sender.sendMessage(prefix + " §cPodczas trybu §estreamowania§c nie można pisać na tym czacie.");
                    return false;
                }
                if (getDataFile().getBoolean(sender.getName() + ".adminchat") == true) {
                    getDataFile().set(sender.getName() + ".adminchat", false);
                    sender.sendMessage("§c§lAdmin§4§lChat §e» §cWyłączono czat.");

                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard admpanel = manager.getNewScoreboard();
                    Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    Player p = (Player) sender;
                    if (!p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §a§lMod§2§lPanel");
                    }
                    if (p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §c§lAdmin§4§lPanel");
                    }
                    p.setScoreboard(emptyBoard);
                    Score nick = objective.getScore("§e» §6" + p.getName());
                    nick.setScore(16);
                    Score czat;
                    if (getDataFile().getString(p.getName() + ".adminchat") == "true") {
                        if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lA§4§lC §ei §a§lM§2§lC");
                        } else {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lAdmin§4§lChat");
                        }
                    } else if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                        czat = objective.getScore("§e» §6Czat §e» " + "§a§lMod§2§lChat");
                    } else {
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
                    } else if (itps >= 8) {
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
                    } else {
                        wyd = "?";
                    }
                    Score wydajnosc = objective.getScore("§e» §6Wydajność §e» " + wyd);
                    wydajnosc.setScore(12);
                    Score tps = objective.getScore("§e» §6TPS §e» " + itps);
                    tps.setScore(11);
                    String kolor2 = "§2";
                    float ms = (float) Bukkit.getAverageTickTime();
                    if (ms <= 45) {
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
                    } else if (memFree >= 1000) {
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

                    saveData();
                    return true;
                } else if (getDataFile().getBoolean(sender.getName() + ".adminchat") == false) {
                    Player p = (Player) sender;
                    getDataFile().set(sender.getName() + ".adminchat", true);
                    sender.sendMessage("§c§lAdmin§4§lChat §e» §aWłączono czat.");

                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard admpanel = manager.getNewScoreboard();
                    Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    if (!p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §a§lMod§2§lPanel");
                    }
                    if (p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §c§lAdmin§4§lPanel");
                    }
                    p.setScoreboard(emptyBoard);
                    Score nick = objective.getScore("§e» §6" + p.getName());
                    nick.setScore(16);
                    Score czat;
                    if (getDataFile().getString(p.getName() + ".adminchat") == "true") {
                        if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lA§4§lC §ei §a§lM§2§lC");
                        } else {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lAdmin§4§lChat");
                        }
                    } else if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                        czat = objective.getScore("§e» §6Czat §e» " + "§a§lMod§2§lChat");
                    } else {
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
                    } else if (itps >= 8) {
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
                    } else {
                        wyd = "?";
                    }
                    Score wydajnosc = objective.getScore("§e» §6Wydajność §e» " + wyd);
                    wydajnosc.setScore(12);
                    Score tps = objective.getScore("§e» §6TPS §e» " + itps);
                    tps.setScore(11);
                    String kolor2 = "§2";
                    float ms = (float) Bukkit.getAverageTickTime();
                    if (ms <= 45) {
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
                    } else if (memFree >= 1000) {
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

                    saveData();
                    return true;
                }
                saveData();
            }
        } else if (cmd.getName().equalsIgnoreCase("mc")) {
            Player p = (Player) sender;
            if (!sender.hasPermission("mod.chat")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. Użyj §e/info§c, aby dowiedzieć się więcej o dostępnych komendach.");
                return false;
            } else {
                if (!getDataFile().contains(sender.getName())) {
                    getDataFile().set(sender.getName() + ":", null);
                    getDataFile().set(sender.getName() + ".modchat", true);
                    getDataFile().set(sender.getName() + ".adminchat", false);
                    sender.sendMessage("§a§lMod§2§lChat §e» §aWłączono czat.");
                    saveData();
                    return true;
                }
                if (getDataFile().getBoolean(sender.getName() + ".stream") == true) {
                    sender.sendMessage(prefix + " §cPodczas trybu §estreamowania§c nie można pisać na tym czacie.");
                    return false;
                }
                if (getDataFile().getBoolean(sender.getName() + ".modchat") == true) {
                    getDataFile().set(sender.getName() + ".modchat", false);
                    sender.sendMessage("§a§lMod§2§lChat §e» §cWyłączono czat.");
                    saveData();
                    return true;
                } else if (getDataFile().getBoolean(sender.getName() + ".modchat") == false) {
                    getDataFile().set(sender.getName() + ".modchat", true);
                    sender.sendMessage("§a§lMod§2§lChat §e» §aWłączono czat.");


                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard admpanel = manager.getNewScoreboard();
                    Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    if (!p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §a§lMod§2§lPanel");
                    }
                    if (p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §c§lAdmin§4§lPanel");
                    }
                    p.setScoreboard(emptyBoard);
                    Score nick = objective.getScore("§e» §6" + p.getName());
                    nick.setScore(16);
                    Score czat;
                    if (getDataFile().getString(p.getName() + ".adminchat") == "true") {
                        if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lA§4§lC §ei §a§lM§2§lC");
                        } else {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lAdmin§4§lChat");
                        }
                    } else if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                        czat = objective.getScore("§e» §6Czat §e» " + "§a§lMod§2§lChat");
                    } else {
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
                    } else if (itps >= 8) {
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
                    } else {
                        wyd = "?";
                    }
                    Score wydajnosc = objective.getScore("§e» §6Wydajność §e» " + wyd);
                    wydajnosc.setScore(12);
                    Score tps = objective.getScore("§e» §6TPS §e» " + itps);
                    tps.setScore(11);
                    String kolor2 = "§2";
                    float ms = (float) Bukkit.getAverageTickTime();
                    if (ms <= 45) {
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
                    } else if (memFree >= 1000) {
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

                    saveData();
                    return true;
                }
                saveData();
            }
        } else if (cmd.getName().equalsIgnoreCase("stream")) {
            if (!sender.hasPermission("panel.mod")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. Użyj §e/info§c, aby dowiedzieć się więcej o dostępnych komendach.");
                return false;
            } else {
                Player p = (Player) sender;
                if (!getDataFile().contains(sender.getName())) {
                    getDataFile().set(sender.getName() + ":", null);
                    getDataFile().set(sender.getName() + ".modchat", false);
                    getDataFile().set(sender.getName() + ".adminchat", false);
                    getDataFile().set(sender.getName() + ".stream", true);
                    sender.sendMessage(prefix + " §aWłączono tryb streamu.");
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    p.setScoreboard(emptyBoard);
                    saveData();
                    return true;
                }
                if (getDataFile().getBoolean(sender.getName() + ".stream") == true) {
                    getDataFile().set(sender.getName() + ".stream", false);
                    sender.sendMessage(prefix + " §cWyłączono tryb streamu.");

                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard admpanel = manager.getNewScoreboard();
                    Objective objective = admpanel.registerNewObjective("test", "dummy", "cokolwiek");

                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    if (!p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §a§lMod§2§lPanel");
                    }
                    if (p.hasPermission("panel.adm")) {
                        objective.setDisplayName(prefix + " §c§lAdmin§4§lPanel");
                    }
                    p.setScoreboard(emptyBoard);
                    Score nick = objective.getScore("§e» §6" + p.getName());
                    nick.setScore(16);
                    Score czat;
                    if (getDataFile().getString(p.getName() + ".adminchat") == "true") {
                        if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lA§4§lC §ei §a§lM§2§lC");
                        } else {
                            czat = objective.getScore("§e» §6Czat §e» " + "§c§lAdmin§4§lChat");
                        }
                    } else if (getDataFile().getString(p.getName() + ".modchat") == "true") {
                        czat = objective.getScore("§e» §6Czat §e» " + "§a§lMod§2§lChat");
                    } else {
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
                    } else if (itps >= 8) {
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
                    } else {
                        wyd = "?";
                    }
                    Score wydajnosc = objective.getScore("§e» §6Wydajność §e» " + wyd);
                    wydajnosc.setScore(12);
                    Score tps = objective.getScore("§e» §6TPS §e» " + itps);
                    tps.setScore(11);
                    String kolor2 = "§2";
                    float ms = (float) Bukkit.getAverageTickTime();
                    if (ms <= 45) {
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
                    } else if (memFree >= 1000) {
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

                    saveData();
                    return true;
                } else if (getDataFile().getBoolean(sender.getName() + ".stream") == false) {
                    getDataFile().set(sender.getName() + ".stream", true);
                    sender.sendMessage(prefix + " §aWłączono tryb streamu.");

                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard emptyBoard = manager.getNewScoreboard();
                    p.setScoreboard(emptyBoard);

                    saveData();
                    return true;
                }
                saveData();
            }
        } else if (cmd.getName().equalsIgnoreCase("checkwarn")) {
            if (!sender.hasPermission("panel.mod")) {
                sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
                return false;
            } else if (args.length == 0) {
                sender.sendMessage("§e§lDragon§6§lCraft§e » §cUżycie: §e/checkwarn <nick>§c.");
            } else {
                List<Punishment> pun = PunishmentManager.get().getPunishments(args[0], null, true);
                sender.sendMessage("§e§lDragon§6§lCraft§e » §cOstrzeżenia §e" + args[0] + "§e:");
                List<String> lista = new ArrayList();

                for (int j = 0; j < pun.size(); ++j) {
                    if (pun.get(j).getType().toString().equalsIgnoreCase("TEMP_WARNING")) {
                        lista.add("§6ID §e» " + pun.get(j).getId() + " §e» §c" + pun.get(j).getReason());
                    }
                }
                if (lista.isEmpty()) {
                    sender.sendMessage("§e» §cbrak");

                } else {
                    for (int j = 0; j < lista.size(); ++j) {
                        sender.sendMessage("§e» " + lista.get(j));
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("gracz")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.chat("/gracz " + p.getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("JaneQ") || args[0].equalsIgnoreCase("NickNickerYT") || args[0].equalsIgnoreCase("MikiIgi192") || args[0].equalsIgnoreCase("kalkulator888")) {
                sender.sendMessage(prefix + " §cPodaj poprawny nick gracza.");
                return false;
            } else if (!args[0].equalsIgnoreCase("JaneQ") && !args[0].equalsIgnoreCase("NickNickerYT") && !args[0].equalsIgnoreCase("MikiIgi192") && !args[0].equalsIgnoreCase("kalkulator888")) {
                if (args.length > 0) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            closeConnection();
                            openConnection();
                            Statement statement = connection.createStatement();
                            ResultSet ogol = statement.executeQuery("SELECT * FROM `staty_ogolem` WHERE nick = '" + args[0] + "'");
                            boolean val = ogol.next();
                            if (!val) {
                                sender.sendMessage(prefix + " §cPodaj poprawny nick gracza.");
                                return;
                            }

                            if (val) {

                                String online = ogol.getString("online");
                                if (online == null) {
                                    online = "?";
                                }
                                String since = ogol.getString("since");
                                if (since == null) {
                                    since = "?";
                                }

                                String serwer_online = ogol.getString("serwer_online");
                                Inventory inv = Bukkit.createInventory(null, 54, "§6Profil §e» §3" + args[0]);
                                ItemStack glowa = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                                ItemStack szklo = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) 0);
                                SkullMeta meta = (SkullMeta) glowa.getItemMeta();
                                meta.setOwner(args[0]);
                                meta.setDisplayName("§6Głowa §e» " + args[0]);
                                glowa.setItemMeta(meta);
                                inv.setItem(4, glowa);

                                for (int i = 0; i < 9; ++i) {
                                    inv.setItem(i + 9, szklo);
                                }
                                ResultSet serwer = statement.executeQuery("SELECT * FROM `" + tabela + "` WHERE nick = '" + args[0] + "'");
                                if (!serwer.next()) {
                                    sender.sendMessage(prefix + " §cPodaj poprawny nick gracza.");
                                    return;
                                }
                                String kille = serwer.getString("kille");
                                String dedy = serwer.getString("dedy");
                                String kdr = serwer.getString("kdr");
                                String ranga = serwer.getString("ranga");
                                String bloki = serwer.getString("bloki");
                                String slub = serwer.getString("slub");

                                if (serwer_online.equalsIgnoreCase("lobby")) {
                                    serwer_online = "Lobby";
                                }
                                if (serwer_online.equalsIgnoreCase("s12")) {
                                    serwer_online = "Survival 1.12";
                                }
                                if (serwer_online.equalsIgnoreCase("s16")) {
                                    serwer_online = "Survival 1.16";
                                }
                                if (serwer_online.equalsIgnoreCase("pvp")) {
                                    serwer_online = "PvP";
                                }

                                ItemStack emeraldBlock;
                                ItemMeta meta3;
                                if (online.equalsIgnoreCase("teraz")) {
                                    emeraldBlock = new ItemStack(Material.LIME_WOOL, 1, (short) 5);
                                    meta3 = emeraldBlock.getItemMeta();
                                    meta3.setDisplayName("§6Status §e» §aonline\n§6Aktualnie na serwerze§e » &6" + serwer_online);
                                    emeraldBlock.setItemMeta(meta3);
                                    inv.setItem(20, emeraldBlock);
                                } else {
                                    emeraldBlock = new ItemStack(Material.RED_WOOL, 1, (short) 14);
                                    meta3 = emeraldBlock.getItemMeta();
                                    meta3.setDisplayName("§6Ostatnio online §e» §c" + online);
                                    emeraldBlock.setItemMeta(meta3);
                                    inv.setItem(20, emeraldBlock);
                                }

                                emeraldBlock = new ItemStack(Material.EMERALD_BLOCK);
                                meta3 = emeraldBlock.getItemMeta();
                                meta3.setDisplayName("§6Ranga §e» " + ranga);
                                emeraldBlock.setItemMeta(meta3);
                                inv.setItem(24, emeraldBlock);
                                ItemStack mapa = new ItemStack(Material.MAP);
                                ItemMeta meta4 = mapa.getItemMeta();
                                if (slub != null && !slub.equalsIgnoreCase("NULL")) {
                                    meta4.setDisplayName("§6Ślub §e» §e" + slub);
                                } else {
                                    meta4.setDisplayName("§6Ślub §e» §ebrak");
                                }

                                mapa.setItemMeta(meta4);
                                inv.setItem(27, mapa);
                                ItemStack kilof = new ItemStack(Material.DIAMOND_PICKAXE);
                                ItemMeta meta5 = kilof.getItemMeta();
                                meta5.setDisplayName("§6Wykopane bloki §e» §e" + bloki);
                                kilof.setItemMeta(meta5);
                                inv.setItem(31, kilof);
                                ItemStack siekiera = new ItemStack(Material.WOODEN_AXE);
                                ItemMeta meta6 = siekiera.getItemMeta();
                                List<Punishment> pun = PunishmentManager.get().getPunishments(args[0], null, true);
                                if (pun.isEmpty()) {
                                    meta6.setDisplayName("§6Kary §e» §abrak");
                                } else {
                                    meta6.setDisplayName("§6Kary §e»");
                                    List<String> lista = new ArrayList();

                                    for (int j = 0; j < pun.size(); ++j) {
                                        String typ;
                                        if (pun.get(j).getType().toString() != "WARNING" && pun.get(j).getType().toString() != "TEMP_WARNING") {
                                            if (pun.get(j).getType().toString() == "BAN") {
                                                typ = "§6Ban";
                                                lista.add("§6Ban" + " §e» §c" + pun.get(j).getReason());
                                            } else {
                                                Date d;
                                                SimpleDateFormat df2;
                                                String data;
                                                if (pun.get(j).getType().toString() == "TEMP_BAN") {
                                                    typ = "§6Ban";
                                                    d = new Date(pun.get(j).getEnd());
                                                    df2 = new SimpleDateFormat("dd.MM.YYYY 'o' HH:mm");
                                                    data = df2.format(d);
                                                    lista.add("§6Ban" + " §e» §c" + pun.get(j).getReason() + ", §6wygasa: §e" + data);
                                                } else if (pun.get(j).getType().toString() == "MUTE") {
                                                    typ = "§6Wyciszenie";
                                                    lista.add("§6Wyciszenie" + " §e» §c" + pun.get(j).getReason());
                                                } else if (pun.get(j).getType().toString() == "TEMP_MUTE") {
                                                    typ = "§6Wyciszenie";
                                                    d = new Date(pun.get(j).getEnd());
                                                    df2 = new SimpleDateFormat("dd.MM.YYYY 'o' HH:mm");
                                                    data = df2.format(d);
                                                    lista.add("§6Wyciszenie" + " §e» §c" + pun.get(j).getReason() + ", §6wygasa: §e" + data);
                                                } else {
                                                    lista.add("§cbłąd");
                                                }
                                            }
                                        } else {
                                            typ = "§6Ostrzeżenie";
                                            lista.add("§6Ostrzeżenie" + " §e» §c" + pun.get(j).getReason());
                                        }

                                        meta6.setLore(lista);
                                    }
                                }

                                siekiera.setItemMeta(meta6);
                                inv.setItem(35, siekiera);
                                ItemStack miecz = new ItemStack(Material.IRON_SWORD);
                                ItemMeta meta7 = miecz.getItemMeta();
                                meta7.setDisplayName("§6Zabójstwa §e» " + kille);
                                List<String> lore = new ArrayList();
                                lore.add("§6Śmierci §e» " + dedy);
                                lore.add("§6Stosunek Z/ś §e» " + kdr);
                                meta7.setLore(lore);
                                miecz.setItemMeta(meta7);
                                inv.setItem(38, miecz);
                                ItemStack jablko = new ItemStack(Material.GOLDEN_APPLE);
                                ItemMeta meta8 = jablko.getItemMeta();
                                meta8.setDisplayName("§6Gra od §e» " + since);
                                jablko.setItemMeta(meta8);
                                inv.setItem(42, jablko);
                                ItemStack wersja = new ItemStack(Material.LEGACY_REDSTONE_TORCH_ON);
                                ItemMeta meta9 = wersja.getItemMeta();
                                meta9.setDisplayName("§6Wersja §e» " + plugin.getDescription().getVersion());
                                wersja.setItemMeta(meta9);
                                inv.setItem(49, wersja);
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    p.openInventory(inv);
                                });
                                statement.close();
                                closeConnection();
                            }
                        } catch (ClassNotFoundException | SQLException var35 ) {
                            logError(ErrorReason.DATABASE);
                            var35.printStackTrace();
                        }

                    });
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("sklepbroadcastdonate")) {
            if (!sender.hasPermission("r.adm")) {
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        }
        final StringBuilder sb = new StringBuilder();
        for (int k = 1; k < args.length; ++k) {
            sb.append(args[k]).append(" ");
        }
        final String allArgs = sb.toString().trim();
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("sklep")));
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &3Gracz &b" + args[0] + "&3 wp\u0142aci\u0142 &b" + allArgs + " &3na rozw\u00f3j serwera!"));
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &3Dzi\u0119kujemy za wsparcie!"));
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &6Smocze jaja&b dost\u0119pne ju\u017c od &61.23z\u0142&b!"));
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e» &bSprawd\u017a ofert\u0119 na &6DCRFT.PL"));
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 8.0f);
        }
        return true;
    }
        return false;
    }
}
