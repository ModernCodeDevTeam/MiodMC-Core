package pl.dcrft.Managers;

import com.sun.corba.se.impl.naming.cosnaming.BindingIteratorImpl;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Panel.PanelType;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static pl.dcrft.Managers.ConfigManger.getDataFile;
import static pl.dcrft.Managers.ConfigManger.getMessagesFile;
import static pl.dcrft.Managers.ConnectionManager.*;
import static pl.dcrft.Managers.DataManager.saveData;
import static pl.dcrft.Managers.Language.LanguageManager.getMessage;
import static pl.dcrft.Managers.Language.LanguageManager.load;
import static pl.dcrft.Managers.MaintenanceManager.*;
import static pl.dcrft.Managers.MessageManager.*;
import static pl.dcrft.Managers.Panel.PanelManager.hidePanel;
import static pl.dcrft.Managers.Panel.PanelManager.updatePanel;
import static pl.dcrft.Managers.Statistic.StatisticManager.showStatistics;
import static pl.dcrft.Utils.ConfigUtil.initializeFiles;
import static pl.dcrft.Utils.GroupUtil.isPlayerInGroup;


public class CommandManager implements CommandExecutor {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();;

    public ArrayList<SessionManager> list = new ArrayList<>();
    String prefix = "prefiks tutaj ";
    @SuppressWarnings({ "unchecked", "unused", "rawtypes" })
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("nieafk")) {
            Player p = (Player) sender;
            boolean sond_on_notafk = Boolean.parseBoolean(plugin.getConfig().getString("afk.sond_on_notafk"));
            if (sond_on_notafk == true) {
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 3);
            } else {

            }
            for (SessionManager sessionManager : list) {
                if (p.getUniqueId() == sessionManager.getPlayer().getUniqueId()) {
                    sessionManager.resetMinute();
                    break;
                }
            }
            p.sendMessage("§7" + plugin.getConfig().getString("afk.prefix") + " " + plugin.getConfig().getString("afk.kick_not_afk_msg"));

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("z") || cmd.getName().equalsIgnoreCase("znajomi") || cmd.getName().equalsIgnoreCase("f")) {
            if(!(sender instanceof Player)){
                sender.sendMessage("Tej komendy nie można wywołać z konsoli.");
                return false;
            }
            Player p = (Player) sender;
            if (args.length == 0) {
                sendPrefixedMessage(p, "friends.help.title");
                    sendMessageList(p, "friends.help.contents");
                return false;
            }
            if (args[0].equalsIgnoreCase("lista") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("info")) {
                sendPrefixedMessage(p, "friends.list.title");
                List<String> znajomi = getDataFile().getStringList(p.getName() + ".znajomi");
                if (znajomi.size() == 0) {
                    sendMessage(p, "friends.list.none");
                    return false;
                } else {
                    for (String s : znajomi) {
                        String online = getDataFile().getString(s + ".online");
                        if (online == null) {
                            sendMessage(p, "friends.list.online");
                        }
                        String msg = MessageFormat.format(getMessage("friends.list.player_format"), s, online);
                        p.sendMessage(msg);
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
                    openConnection();
                    try {
                        Statement statement = connection.createStatement();
                        String updatep = "UPDATE `" + tabela + "` SET slub = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "' WHERE nick = '" + p.getName() + "'";
                        String updateo = "UPDATE `" + tabela + "` SET slub = '" + p.getName() + "' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                        statement.executeUpdate(updatep);
                        statement.executeUpdate(updateo);
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
                openConnection();
                try {
                    Statement statement = connection.createStatement();
                    String updatep = "UPDATE `" + tabela + "` SET slub = 'NULL' WHERE nick = '" + p.getName() + "'";
                    String updateo = "UPDATE `" + tabela + "` SET slub = 'NULL' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                    statement.executeUpdate(updatep);
                    statement.executeUpdate(updateo);
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
                for (String arg : args) {
                    sb.append(arg).append(" ");
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
                    sendPrefixedMessage((Player) sender, "notfound");
                    return false;
                }
                final String sub = args[0];
                if (sub.equalsIgnoreCase("przeladuj")) {
                    saveData();
                    initializeFiles();

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
                stopServer();
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("reload60")) {
            if (sender.hasPermission("r.adm")) {
                reloadServer();
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("restart60")) {
            if (sender.hasPermission("r.adm")) {
                restartServer();
                return true;
            }
            sender.sendMessage(prefix + " §cNie ma takiej komendy. U\u017cyj §e/info§c, aby dowiedzie\u0107 si\u0119 wi\u0119cej o dost\u0119pnych komendach.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("przerwa60")) {
            if (sender.hasPermission("r.adm")) {
                maintenanceStart();
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
            Player p = (Player) sender;
            if (!sender.hasPermission("admin.chat")) {
                sendPrefixedMessage(p, "notfound");
                return false;
            } else {
                if (!getDataFile().contains(sender.getName())) {
                    getDataFile().set(sender.getName() + ":", null);
                    getDataFile().set(sender.getName() + ".adminchat", true);
                    getDataFile().set(sender.getName() + ".modchat", false);
                    sender.sendMessage("§c§lAdmin§4§lChat §e» §aWłączono czat.");

                    if(p.hasPermission("panel.adm")){
                        updatePanel(p, PanelType.ADMIN);
                    }
                    else if(p.hasPermission("panel.mod")){
                        updatePanel(p, PanelType.MOD);
                    }

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


                    if(p.hasPermission("panel.adm")){
                        updatePanel(p, PanelType.ADMIN);
                    }
                    else if(p.hasPermission("panel.mod")){
                        updatePanel(p, PanelType.MOD);
                    }

                    saveData();
                    return true;
                } else if (getDataFile().getBoolean(sender.getName() + ".adminchat") == false) {
                    getDataFile().set(sender.getName() + ".adminchat", true);
                    sender.sendMessage("§c§lAdmin§4§lChat §e» §aWłączono czat.");
                    if(p.hasPermission("panel.adm")){
                        updatePanel(p, PanelType.ADMIN);
                    }
                    else if(p.hasPermission("panel.mod")){
                        updatePanel(p, PanelType.MOD);
                    }
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
                if(p.hasPermission("panel.adm")){
                    updatePanel(p, PanelType.ADMIN);
                }
                else if(p.hasPermission("panel.mod")){
                    updatePanel(p, PanelType.MOD);
                }
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
                    hidePanel(p);
                    saveData();
                    return true;
                }
                if (getDataFile().getBoolean(sender.getName() + ".stream") == true) {
                    getDataFile().set(sender.getName() + ".stream", false);
                    sender.sendMessage(prefix + " §cWyłączono tryb streamu.");

                    if(p.hasPermission("panel.adm")){
                        updatePanel(p, PanelType.ADMIN);
                    }
                    else if(p.hasPermission("panel.mod")){
                        updatePanel(p, PanelType.MOD);
                    }

                    saveData();
                    return true;
                } else if (getDataFile().getBoolean(sender.getName() + ".stream") == false) {
                    getDataFile().set(sender.getName() + ".stream", true);
                    sender.sendMessage(prefix + " §aWłączono tryb streamu.");
                    hidePanel(p);
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

                for (Punishment punishment : pun) {
                    if (punishment.getType().toString().equalsIgnoreCase("TEMP_WARNING")) {
                        lista.add("§6ID §e» " + punishment.getId() + " §e» §c" + punishment.getReason());
                    }
                }
                if (lista.isEmpty()) {
                    sender.sendMessage("§e» §cbrak");

                } else {
                    for (String s : lista) {
                        sender.sendMessage("§e» " + s);
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
                    showStatistics(p, args);
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
