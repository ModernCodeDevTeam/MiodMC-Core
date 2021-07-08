package pl.dcrft.Managers;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Panel.PanelType;
import pl.dcrft.Utils.ConfigUtil;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static pl.dcrft.Managers.DatabaseManager.*;
import static pl.dcrft.Managers.LanguageManager.getMessage;
import static pl.dcrft.Managers.MaintenanceManager.*;
import static pl.dcrft.Managers.MessageManager.*;
import static pl.dcrft.Managers.Panel.PanelManager.hidePanel;
import static pl.dcrft.Managers.Panel.PanelManager.updatePanel;
import static pl.dcrft.Managers.Statistic.StatisticGUIManager.showStatistics;
import static pl.dcrft.Utils.ConfigUtil.initializeFiles;
import static pl.dcrft.Utils.GroupUtil.isPlayerInGroup;


public class CommandManager implements CommandExecutor {
    private static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public final ArrayList<SessionManager> list = new ArrayList<>();
    final String prefix = LanguageManager.getMessage("prefix");

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("nieafk")) {
            Player p = (Player) sender;
            boolean sound_on_notafk = Boolean.parseBoolean(plugin.getConfig().getString("afk.sound_on_notafk"));
            if (sound_on_notafk) {
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 3);
            }
            for (SessionManager sessionManager : list) {
                if (p.getUniqueId() == sessionManager.getPlayer().getUniqueId()) {
                    sessionManager.resetMinute();
                    break;
                }
            }
            MessageManager.sendPrefixedMessage(p, "afk.kick_not_afk_msg");

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("z") || cmd.getName().equalsIgnoreCase("znajomi") || cmd.getName().equalsIgnoreCase("f")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(LanguageManager.getMessage("console_error"));
                return false;
            }
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "friends.help.title");
                sendMessageList(p, "friends.help.contents");
                return false;
            }
            if (args[0].equalsIgnoreCase("lista") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("info")) {
                MessageManager.sendPrefixedMessage(p, "friends.list.title");
                List<String> znajomi = ConfigManager.getDataFile().getStringList("players." + p.getName() + ".znajomi");
                if (znajomi.size() == 0) {
                    sendMessage(p, "friends.list.none");
                } else {
                    for (String s : znajomi) {
                        String online = ConfigManager.getDataFile().getString("players." + s + ".online");
                        String msg = MessageFormat.format(LanguageManager.getMessage("friends.list.player_format"), s, online);
                        if (online == null) {
                            msg = MessageFormat.format(LanguageManager.getMessage("friends.list.player_format"), s, LanguageManager.getMessage("friends.list.online"));
                        }
                        p.sendMessage(msg);
                    }
                }
                return false;
            }
            if (args[0].equalsIgnoreCase("usun") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("u") || args[0].equalsIgnoreCase("wyrzuc")) {
                if (args.length == 1) {
                    MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                } else {
                    List<String> znajomip = ConfigManager.getDataFile().getStringList("players." + sender.getName() + ".znajomi");
                    List<String> znajomio = ConfigManager.getDataFile().getStringList("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi");
                    if (!znajomip.contains(args[1])) {
                        sender.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.remove.not_your_friend"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    } else {
                        znajomip.remove(Bukkit.getOfflinePlayer(args[1]).getName());
                        znajomio.remove(sender.getName());
                        ConfigManager.getDataFile().set("players." + sender.getName() + ".znajomi", znajomip);
                        ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi", znajomio);
                        ConfigManager.saveData();
                        sender.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.remove.removed"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    }
                }
                return false;
            }
            if (args[0].equalsIgnoreCase("dodaj") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("d")) {
                if (args.length == 1) {
                    MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                    return false;
                }
                if (args[1].equalsIgnoreCase(sender.getName())) {
                    MessageManager.sendPrefixedMessage(p, "friends.add.self");
                    return false;
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                    return false;
                }
                if (!Bukkit.getPlayer(args[1]).isOnline()) {
                    MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                    return false;
                }
                if (plugin.getConfig().getStringList("staff").contains(args[1])) {
                    MessageManager.sendPrefixedMessage(p, "friends.add.staff");
                    return false;
                } else {
                    Player o = Bukkit.getPlayer(args[1]);
                    List<String> znajomip = ConfigManager.getDataFile().getStringList("players." + sender.getName() + ".znajomi");
                    if (znajomip.contains(o.getName())) {
                        MessageManager.sendPrefixedMessage(p, "friends.add.already_friend");
                        return false;
                    }
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".znajprosba." + Bukkit.getOfflinePlayer(args[1]).getName(), true);
                    ConfigManager.saveData();
                    o.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.notification.title"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    o.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.notification.accept"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    o.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.notification.cancel"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    sender.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.invited"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("akceptuj") || args[0].equalsIgnoreCase("a")) {
                if (args.length == 1) {
                    MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                    return false;
                }
                if (!ConfigManager.getDataFile().getBoolean("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName())) {
                    MessageManager.sendPrefixedMessage(p, "friends.accept.invitation_not_send");
                    return false;
                } else {
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName(), null);
                    List<String> znajomip = ConfigManager.getDataFile().getStringList("players." + sender.getName() + ".znajomi");
                    List<String> znajomio = ConfigManager.getDataFile().getStringList("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi");
                    znajomip.add(Bukkit.getOfflinePlayer(args[1]).getName());
                    znajomio.add(sender.getName());
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".znajomi", znajomip);
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajomi", znajomio);
                    ConfigManager.saveData();
                    if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                        Bukkit.getPlayer(args[1]).sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.accept.accepted_self"), sender.getName()));
                    }
                    sender.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.accept.accepted_target"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("odrzuc") || args[0].equalsIgnoreCase("o")) {
                if (args.length == 1) {
                    MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                    return false;
                }
                if (!ConfigManager.getDataFile().getBoolean("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName())) {
                    MessageManager.sendPrefixedMessage(p, "friends.reject.invitation_not_send");
                    return false;
                } else {
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[1]).getName() + ".znajprosba." + sender.getName(), null);
                    ConfigManager.saveData();
                    if (Bukkit.getPlayer(args[1]) != null && Bukkit.getPlayer(args[1]).isOnline()) {
                        Bukkit.getPlayer(args[1]).sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.reject.rejected_self"), sender.getName()));
                    }
                    sender.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.reject.rejected_target"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    return false;
                }
            } else {
                MessageManager.sendPrefixedMessage(p, "friends.help.title");
                sendMessageList(p, "friends.help.contents");
                return false;
            }
        }


        if (cmd.getName().equalsIgnoreCase("slub")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            if (!Bukkit.getPlayer(args[0]).isOnline()) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            Player other = Bukkit.getPlayer(args[0]);
            if (plugin.getConfig().getStringList("staff").contains(args[1])) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            if (args[0].equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.send.self");
                return false;
            } else {
                if (ConfigManager.getDataFile().getString("players." + p.getName() + ".slub") != null) {
                    MessageManager.sendPrefixedMessage(p, "marry.already");
                    return false;
                }
                if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub") != null) {
                    MessageManager.sendPrefixedMessage(p, "marry.target_already");
                    return false;
                } else {
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", Bukkit.getOfflinePlayer(args[0]).getName());
                    ConfigManager.saveData();
                    MessageManager.sendPrefixedMessage(p, "marry.send.sent.self");

                    other.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.send.sent.target"), p.getName()));
                    return true;
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("sakceptuj")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            if (ConfigManager.getDataFile().getString("players." + p.getName() + ".slub") != null) {
                MessageManager.sendPrefixedMessage(p, "marry.already");
                return false;
            }
            if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !ConfigManager.getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.no_ivite_send");
                return false;
            } else {
                if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + "slub") != null) {
                    MessageManager.sendPrefixedMessage(p, "marry.target_already");
                    return false;
                }
                if (!p.getInventory().contains(Material.DIAMOND)) {
                    MessageManager.sendPrefixedMessage(p, "marry.accept.missing_diamond");
                    return false;
                } else {
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                    if (Bukkit.getPlayer(args[0]).isOnline()) {
                        MessageManager.sendPrefixedMessage(Bukkit.getPlayer(args[0]), "marry.accept.accepted");
                    }
                    MessageManager.sendPrefixedMessage(p, "marry.accept.accepted");
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slub", Bukkit.getOfflinePlayer(args[0]).getName());
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", p.getName());
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", null);
                    ConfigManager.saveData();
                    openConnection();
                    try {
                        Statement statement = connection.createStatement();
                        String updatep = "UPDATE `" + table + "` SET slub = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "' WHERE nick = '" + p.getName() + "'";
                        String updateo = "UPDATE `" + table + "` SET slub = '" + p.getName() + "' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                        statement.executeUpdate(updatep);
                        statement.executeUpdate(updateo);
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    plugin.getServer().broadcastMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.accept.broadcast"), p.getName(), Bukkit.getOfflinePlayer(args[0]).getName()));
                    return false;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("sodrzuc")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
            } else if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !ConfigManager.getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.no_ivite_send");
            } else {
                MessageManager.sendPrefixedMessage(p, "marry.reject.rejected");
                if (Bukkit.getPlayer(args[0]).isOnline()) {
                    MessageManager.sendPrefixedMessage(Bukkit.getPlayer(args[0]), "marry.reject.rejected");
                }
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                ConfigManager.saveData();
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("rozwod")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
            } else if (args[0].equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.self");
            } else if (ConfigManager.getDataFile().getString("players." + p.getName() + ".slub") == null) {
                MessageManager.sendPrefixedMessage(p, "marry.not_in");
            } else if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub") == null || !ConfigManager.getDataFile().getString(Bukkit.getOfflinePlayer(args[0]).getName() + ".slub").equalsIgnoreCase(p.getName()) || !ConfigManager.getDataFile().getString(p.getName() + ".slub").equalsIgnoreCase(Bukkit.getOfflinePlayer(args[0]).getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.not_with_target");
            } else {
                ConfigManager.getDataFile().set("players." + p.getName() + ".slub", null);
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", null);
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", null);
                ConfigManager.saveData();
                openConnection();
                try {
                    Statement statement = connection.createStatement();
                    String updatep = "UPDATE `" + table + "` SET slub = 'NULL' WHERE nick = '" + p.getName() + "'";
                    String updateo = "UPDATE `" + table + "` SET slub = 'NULL' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                    statement.executeUpdate(updatep);
                    statement.executeUpdate(updateo);
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                plugin.getServer().broadcastMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.reject.broadcast"), p.getName(), Bukkit.getOfflinePlayer(args[0]).getName()));

            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("czat")) {
            if (!sender.hasPermission("panel.mod")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            } else {
                if (ConfigManager.getDataFile().getBoolean("czat")) {
                    ConfigManager.getDataFile().set("czat", false);
                    ConfigManager.saveData();
                    plugin.getServer().broadcastMessage(prefix + LanguageManager.getMessage("chat.disabled"));
                } else {
                    ConfigManager.getDataFile().set("czat", true);
                    ConfigManager.saveData();
                    plugin.getServer().broadcastMessage(prefix + LanguageManager.getMessage("chat.enabled"));
                }
            }
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("pomoc")) {
            MessageManager.sendPrefixedMessage(sender, "help.title");
            MessageManager.sendMessageList(sender, "help.contents");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("vip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.vip"));
            } else {
                MessageManager.sendMessageList(p, "ranks.vip");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("svip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.svip"));
            } else {
                MessageManager.sendMessageList(p, "ranks.svip");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("mvip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.mvip"));
            } else {
                MessageManager.sendMessageList(p, "ranks.mvip");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("evip")) {
            Player p = (Player) sender;
            final boolean grupa = isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.evip"));
            } else {
                MessageManager.sendMessageList(p, "ranks.evip");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("cc")) {
            if (sender.hasPermission("cc.adm")) {
                for (int i = 0; i < 100; ++i) {
                    Bukkit.getServer().broadcastMessage("");
                }
                Bukkit.getServer().broadcastMessage(prefix + getMessage("chat.cleared"));
                return true;
            }
            if (!sender.hasPermission("cc.adm")) {
                sendPrefixedMessage(sender, "notfound");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("dcccast") && args.length != 0) {
            if (sender.hasPermission("dcc.adm")) {
                final StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg).append(" ");
                }
                final String allArgs = sb.toString().trim();
                Bukkit.getServer().broadcastMessage(prefix + ChatColor.translateAlternateColorCodes('&', allArgs));
            } else {
                sendPrefixedMessage(sender, "notfound");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("dcc")) {
            if (args.length == 0) {
                if (sender.hasPermission("dcc.adm")) {
                    sender.sendMessage("§e§lDragon§6§lCraft§b§lCore " + plugin.getDescription().getVersion());
                    MessageManager.sendMessageList(sender, "pluginhelp.contents");
                } else {
                    sendPrefixedMessage(sender, "notfound");
                }
            } else {
                if (!sender.hasPermission("dcc.adm")) {
                    MessageManager.sendPrefixedMessage(sender, "notfound");
                }
                final String sub = args[0];
                if (sub.equalsIgnoreCase("przeladuj")) {
                    ConfigUtil.reloadFiles();
                    MessageManager.sendPrefixedMessage(sender, "maintenance.reload_plugin");
                } else if (sub.equalsIgnoreCase("afk")) {
                    String kick_msg = plugin.getConfig().getString("afk.kick_msg");
                    int kick_warn_delay = plugin.getConfig().getInt("afk.kick_warn_delay");
                    String kick_warn_msg = plugin.getConfig().getString("afk.kick_warn_msg");
                    String kick_warn_msg_afk = plugin.getConfig().getString("afk.kick_warn_msg_afk");
                    int kick_delay = plugin.getConfig().getInt("afk.kick_delay");
                    String sound_on_get_warn = plugin.getConfig().getString("afk.sound_on_get_warn");
                    String sound_on_notafk = plugin.getConfig().getString("afk.sond_on_notafk");
                    String notafkmsg = plugin.getConfig().getString("afk.kick_not_afk_msg");

                    sender.sendMessage("§e§lDragon§6§lCraft§b§lCore " + plugin.getDescription().getVersion());
                    MessageManager.sendMessage(sender, "pluginhelp.afk.title");
                    sender.sendMessage(getMessage("pluginhelp.afk.kick_msg") + " " + kick_msg);
                    sender.sendMessage(plugin.getConfig().getString("afk.kick_warn_delay") + " " + kick_warn_delay);
                    sender.sendMessage(getMessage("pluginhelp.afk.kick_warn_msg") + " " + kick_warn_msg);
                    sender.sendMessage(getMessage("pluginhelp.afk.kick_warn_msg_afk") + " " + kick_warn_msg_afk);
                    sender.sendMessage(getMessage("pluginhelp.afk.notafkmsg") + " " + notafkmsg);
                    sender.sendMessage(plugin.getConfig().getString("afk.kick_delay") + " " + kick_delay);
                    sender.sendMessage(plugin.getConfig().getString("afk.sound_on_get_warn") + " " + sound_on_get_warn);
                    sender.sendMessage(plugin.getConfig().getString("afk.sound_on_notafk") + " " + sound_on_notafk);
                } else if (sub.equalsIgnoreCase("anvil")) {
                    if (!(sender instanceof Player)) {
                        sendPrefixedMessage(sender, "console_error");
                    } else {
                        Player p = (Player) sender;
                        if (p.getTargetBlock(null, 100) != null) {
                            Block block = p.getTargetBlock(null, 100);

                            if (block.getType() == Material.ANVIL || block.getType() == Material.CHIPPED_ANVIL || block.getType() == Material.DAMAGED_ANVIL) {

                                Location loc = block.getLocation();

                                FileConfiguration data = ConfigManager.getDataFile();

                                Set<String> anvils = data.getConfigurationSection("anvils").getKeys(false);

                                int max = 0;
                                if (anvils != null) {
                                    for (String i : anvils) {
                                        int x = data.getInt("anvils." + i + ".x");
                                        int y = data.getInt("anvils." + i + ".y");
                                        int z = data.getInt("anvils." + i + ".z");
                                        String world = data.getString("anvils." + i + ".world");
                                        Location al = new Location(Bukkit.getWorld(world), x, y, z);
                                        if (loc.equals(al)) {
                                            data.set("anvils." + i, null);
                                            sendPrefixedMessage(p, "anvils.deleted");
                                            ConfigManager.saveData();
                                            return true;
                                        }
                                        if (Integer.valueOf(i) > max) {
                                            max = Integer.valueOf(i) + 1;
                                        }
                                    }
                                }

                                int x = loc.getBlockX();
                                int y = loc.getBlockY();
                                int z = loc.getBlockZ();
                                String world = loc.getWorld().getName();

                                ConfigManager.getDataFile().set("anvils." + max + ".x", x);
                                ConfigManager.getDataFile().set("anvils." + max + ".y", y);
                                ConfigManager.getDataFile().set("anvils." + max + ".z", z);
                                ConfigManager.getDataFile().set("anvils." + max + ".world", world);
                                sendPrefixedMessage(p, "anvils.created");

                                ConfigManager.saveData();
                            } else {
                                sendPrefixedMessage(p, "anvils.not_an_anvil");
                            }

                        } else {
                            sendPrefixedMessage(p, "anvils.not_an_anvil");
                        }
                    }
                } else if (sub.equalsIgnoreCase("block")) {
                    if (args.length < 2) {
                        sendPrefixedMessage(sender, "block.usage");
                    } else {
                        String toblock = args[1].replace(":", "%colon%");
                        if (ConfigManager.getDisabledFile().get(toblock) != null) {
                            sendPrefixedMessage(sender, "block.already");
                        } else {
                            if (args.length == 2) {
                                ConfigManager.getDisabledFile().set(toblock + ".Message", getMessage("prefix") + getMessage("notfound"));
                                ConfigManager.saveDisabledFile();
                                sendPrefixedMessage(sender, "block.blocked");
                            }
                            else if (args.length > 2) {

                                final StringBuilder sb = new StringBuilder();
                                for (int k = 2; k < args.length; ++k) {
                                    sb.append(args[k]).append(" ");
                                }
                                final String message = sb.toString().trim();

                                ConfigManager.getDisabledFile().set(toblock + ".Message", message);
                                ConfigManager.saveDisabledFile();
                                sendPrefixedMessage(sender, "block.blocked");
                            }
                        }
                    }
                } else if (sub.equalsIgnoreCase("unblock")) {
                    if (args.length < 2) {
                        sendPrefixedMessage(sender, "unblock.usage");
                    } else {
                        String tounblock = args[1].replace(":", "%colon%");
                        if (ConfigManager.getDisabledFile().get(tounblock) == null) {
                            sendPrefixedMessage(sender, "unblock.notfound");
                        } else {
                                ConfigManager.getDisabledFile().set(tounblock, null);
                                ConfigManager.saveDisabledFile();
                                sendPrefixedMessage(sender, "unblock.unblocked");
                        }
                    }
                } else {
                    sender.sendMessage("§e§lDragon§6§lCraft§b§lCore " + plugin.getDescription().getVersion());
                    MessageManager.sendMessageList(sender, "pluginhelp.contents");
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("losuj") && args.length == 0) {
            if (sender.hasPermission("panel.adm")) {
                Bukkit.getServer().broadcastMessage(prefix + LanguageManager.getMessage("randomizer.broadcast"));
                final Random rand = new Random();
                final String randomElement = LanguageManager.getMessageList("ramdomizer.list").get(rand.nextInt(LanguageManager.getMessageList("ramdomizer.list").size()));
                Bukkit.getServer().broadcastMessage(prefix + LanguageManager.getMessage("ramdomizer.list") + randomElement);
                return true;
            }
            MessageManager.sendPrefixedMessage(sender, "notfound");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("stop")) {
            if (sender.hasPermission("r.adm")) {
                if (args.length == 0) {
                    stopServer();
                } else {
                    if (args[0].contains("[0-9]+")) {
                        sendPrefixedMessage(sender, "maintenance.wrong_value");
                    } else {
                        stopServer(Integer.valueOf(args[0]));
                    }
                }
            } else {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            }
        } else if (cmd.getName().equalsIgnoreCase("reload")) {
            if (sender.hasPermission("r.adm")) {
                if (args.length == 0) {
                    reloadServer();
                } else {
                    if (args[0].contains("[0-9]+")) {
                        sendPrefixedMessage(sender, "maintenance.wrong_value");
                    } else {
                        reloadServer(Integer.valueOf(args[0]));
                    }
                }
            } else {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("restart")) {
            if (sender.hasPermission("r.adm")) {
                if (args.length == 0) {
                    restartServer();
                } else {
                    if (args[0].contains("[0-9]+")) {
                        sendPrefixedMessage(sender, "maintenance.wrong_value");
                    } else {
                        restartServer(Integer.valueOf(args[0]));
                    }
                }
            } else {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            }
        } else if (cmd.getName().equalsIgnoreCase("przerwa")) {
            if (sender.hasPermission("r.adm")) {
                if (args.length == 0) {
                    maintenanceStart();
                } else {
                    if (args[0].contains("[0-9]+")) {
                        sendPrefixedMessage(sender, "maintenance.wrong_value");
                    } else {
                        maintenanceStart(Integer.valueOf(args[0]));
                    }
                }
            } else {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            }
        } else if (cmd.getName().equalsIgnoreCase("ac")) {
            if (!(sender instanceof Player)) {
                MessageManager.sendMessage(sender, "console_error");
                return false;
            }
            if (!sender.hasPermission("admin.chat")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
                return false;
            } else {
                Player p = (Player) sender;
                if (!ConfigManager.getDataFile().contains("players." + sender.getName())) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ":", null);
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".adminchat", true);
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".modchat", false);
                    sender.sendMessage(LanguageManager.getMessage("staffchat.adminchat.title") + LanguageManager.getMessage("staffchat.modchat.spacer") + LanguageManager.getMessage("staffchat.enabled"));

                    if (sender.hasPermission("panel.adm")) {
                        updatePanel(p, PanelType.ADMIN);
                    } else if (sender.hasPermission("panel.mod")) {
                        updatePanel(p, PanelType.MOD);
                    }
                    ConfigManager.saveData();
                    return true;
                }
                if (ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".stream")) {
                    MessageManager.sendPrefixedMessage(sender, "staffchat.stream.error");
                    return false;
                }
                if (ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".adminchat")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".adminchat", false);
                    sender.sendMessage(LanguageManager.getMessage("staffchat.adminchat.title") + LanguageManager.getMessage("staffchat.modchat.spacer") + LanguageManager.getMessage("staffchat.disabled"));


                    if (p.hasPermission("panel.adm")) {
                        updatePanel(p, PanelType.ADMIN);
                    } else if (p.hasPermission("panel.mod")) {
                        updatePanel(p, PanelType.MOD);
                    }

                    ConfigManager.saveData();
                    return true;
                } else if (!ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".adminchat")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".adminchat", true);
                    sender.sendMessage(LanguageManager.getMessage("staffchat.adminchat.title") + LanguageManager.getMessage("staffchat.modchat.spacer") + LanguageManager.getMessage("staffchat.enabled"));
                    if (p.hasPermission("panel.adm")) {
                        updatePanel(p, PanelType.ADMIN);
                    } else if (p.hasPermission("panel.mod")) {
                        updatePanel(p, PanelType.MOD);
                    }
                    ConfigManager.saveData();
                    return true;
                }
                ConfigManager.saveData();
            }
        } else if (cmd.getName().equalsIgnoreCase("mc")) {
            Player p = (Player) sender;
            if (!sender.hasPermission("mod.chat")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
                return false;
            } else {
                if (p.hasPermission("panel.adm")) {
                    updatePanel(p, PanelType.ADMIN);
                } else if (p.hasPermission("panel.mod")) {
                    updatePanel(p, PanelType.MOD);
                }
                if (!ConfigManager.getDataFile().contains("players." + sender.getName())) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ":", null);
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".modchat", true);
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".adminchat", false);
                    sender.sendMessage(LanguageManager.getMessage("staffchat.modchat.title") + LanguageManager.getMessage("staffchat.modchat.spacer") + LanguageManager.getMessage("staffchat.enabled"));
                    ConfigManager.saveData();
                    return true;
                }
                if (ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".stream")) {
                    MessageManager.sendPrefixedMessage(sender, "staffchat.stream.error");
                    return false;
                }
                if (ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".modchat")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".modchat", false);
                    sender.sendMessage(LanguageManager.getMessage("staffchat.modchat.title") + LanguageManager.getMessage("staffchat.modchat.spacer") + LanguageManager.getMessage("staffchat.disabled"));
                    ConfigManager.saveData();
                    return true;
                } else if (!ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".modchat")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".modchat", true);
                    sender.sendMessage(LanguageManager.getMessage("staffchat.modchat.title") + LanguageManager.getMessage("staffchat.modchat.spacer") + LanguageManager.getMessage("staffchat.enabled"));
                    ConfigManager.saveData();
                    return true;
                }
                ConfigManager.saveData();
            }
        } else if (cmd.getName().equalsIgnoreCase("stream")) {
            if (!sender.hasPermission("panel.mod")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
                return false;
            } else {
                Player p = (Player) sender;
                if (!ConfigManager.getDataFile().contains("players." + sender.getName())) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ":", null);
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".modchat", false);
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".adminchat", false);
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".stream", true);
                    sendPrefixedMessage(sender, "staffchat.stream.enabled");
                    hidePanel(p);
                    ConfigManager.saveData();
                    return true;
                }
                if (ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".stream")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".stream", false);
                    sendPrefixedMessage(sender, "staffchat.stream.disabled");

                    if (p.hasPermission("panel.adm")) {
                        updatePanel(p, PanelType.ADMIN);
                    } else if (p.hasPermission("panel.mod")) {
                        updatePanel(p, PanelType.MOD);
                    }

                    ConfigManager.saveData();
                    return true;
                } else if (!ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".stream")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".stream", true);
                    sendPrefixedMessage(sender, "staffchat.stream.enabled");
                    hidePanel(p);
                    ConfigManager.saveData();
                    return true;
                }
                ConfigManager.saveData();
            }
        } else if (cmd.getName().equalsIgnoreCase("checkwarn")) {
            if (!sender.hasPermission("panel.mod")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
                return false;
            } else if (args.length == 0) {
                sendPrefixedMessage(sender, "checkwarn.usage");
            } else {
                List<Punishment> pun = PunishmentManager.get().getPunishments(args[0], null, true);
                sender.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("checkwarn.title"), args[0]));
                ArrayList<String> lista = new ArrayList<>();

                for (Punishment punishment : pun) {
                    if (punishment.getType().toString().equalsIgnoreCase("TEMP_WARNING")) {
                        lista.add(MessageFormat.format(LanguageManager.getMessage("checkwarn.list"), punishment.getId(), punishment.getReason()));
                    }
                }
                if (lista.isEmpty()) {
                    MessageManager.sendMessage(sender, "checkwarn.none");

                } else {
                    for (Object s : lista) {
                        sender.sendMessage(LanguageManager.getMessage("checkwarn.list_prefix") + s);
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("gracz")) {
            if (!(sender instanceof Player)) {
                MessageManager.sendPrefixedMessage(sender, "console_error");
            } else {
                Player p = (Player) sender;
                if (args.length == 0) {
                    p.chat("/gracz " + p.getName());
                } else if (plugin.getConfig().getStringList("staff").contains(args[0])) {
                    sendPrefixedMessage(sender, "wrong_player_nickname");
                } else {
                    showStatistics(p, args[0]);
                }
            }
            return false;
        } else if (cmd.getName().equalsIgnoreCase("sklepbroadcast")) {
            if (!sender.hasPermission("r.adm")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            } else if (args.length == 0) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            } else if (args.length == 1) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            } else {
                final StringBuilder sb = new StringBuilder();
                for (int k = 1; k < args.length; ++k) {
                    sb.append(args[k]).append(" ");
                }
                final String allArgs = sb.toString().trim();
                Bukkit.getServer().broadcastMessage(prefix + "" + LanguageManager.getMessage("shopbroadcast.title"));
                Bukkit.getServer().broadcastMessage(MessageFormat.format(getMessage("shopbroadcast.purchase"), args[0], allArgs));
                for (String msg : LanguageManager.getMessageList("shopbroadcast.other")) {
                    Bukkit.getServer().broadcastMessage(msg);
                }
                Sound sound = Sound.valueOf(plugin.getConfig().getString("shop_purchase.sound"));
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), sound, 1.0f, 8.0f);
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("sklepbroadcastdonate")) {
            if (!sender.hasPermission("r.adm")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            } else if (args.length == 0) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            } else if (args.length == 1) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            } else {
                final StringBuilder sb = new StringBuilder();
                for (int k = 1; k < args.length; ++k) {
                    sb.append(args[k]).append(" ");
                }
                final String allArgs = sb.toString().trim();
                Bukkit.getServer().broadcastMessage(prefix + "" + LanguageManager.getMessage("shopbroadcast.title"));
                Bukkit.getServer().broadcastMessage(MessageFormat.format(getMessage("shopbroadcast.donate"), args[0], allArgs));
                for (String msg : LanguageManager.getMessageList("shopbroadcast.other")) {
                    Bukkit.getServer().broadcastMessage(msg);
                }
                Sound sound = Sound.valueOf(plugin.getConfig().getString("shop_purchase.sound"));
                for (final Player player : Bukkit.getOnlinePlayers()) {

                    player.playSound(player.getLocation(), sound, 1.0f, 8.0f);
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("alias")) {
            if (sender.hasPermission("dcc.adm")) {
                if (args.length == 0) {
                    sendPrefixedMessage(sender, "aliases.help.header");
                    sendMessageList(sender, "aliases.help.contents");
                    return true;
                }
                String subCommand = args[0].toLowerCase();
                if (subCommand.equals("dodaj") || subCommand.equals("usun") || subCommand.equals("lista")) {
                    Map<String, Object> map = plugin.getConfig().getConfigurationSection("aliases").getValues(false);
                    if (subCommand.equalsIgnoreCase("dodaj")) {
                        if (args.length > 2) {
                            String name = args[1];
                            if (map.get(name) == null) {
                                final StringBuilder sb = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    sb.append(" ").append(args[i]);
                                }
                                final String allArgs = sb.toString().trim();
                                plugin.getConfig().set("aliases." + name, allArgs);
                                plugin.saveConfig();
                                String msg = MessageFormat.format(LanguageManager.getMessage("aliases.add.added"), name, allArgs);
                                sender.sendMessage(prefix + msg);
                            } else {
                                sendPrefixedMessage(sender, "aliases.add.exists");
                            }
                        } else {
                            sendPrefixedMessage(sender, "aliases.add.usage");
                        }
                    } else if (subCommand.equals("usun")) {
                        if (args.length > 1) {
                            if (map.get(args[1]) == null) {
                                sendPrefixedMessage(sender, "aliases.delete.notfound");
                            } else {
                                plugin.getConfig().set("aliases." + args[1], null);
                                plugin.saveConfig();
                                sendPrefixedMessage(sender, "aliases.delete.deleted");
                            }
                        } else {
                            sendPrefixedMessage(sender, "aliases.delete.usage");
                        }
                    } else if (subCommand.equals("lista")) {
                        sendPrefixedMessage(sender, "aliases.help.header");
                        for (String key : map.keySet()) {
                            String value = map.get(key).toString();
                            sender.sendMessage(
                                    getMessage("aliases.list.spacer") +
                                            " " + getMessage("aliases.list.from") +
                                            key +
                                            " " + getMessage("aliases.list.spacer") +
                                            " " + getMessage("aliases.list.to") +
                                            value);
                        }
                    }
                } else {
                    sendPrefixedMessage(sender, "aliases.help.header");
                    sendMessageList(sender, "aliases.help.contents");
                }
            } else {
                sendPrefixedMessage(sender, "notfound");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("+")) {
            if (!sender.hasPermission(plugin.getConfig().getString("timedpermission"))) {
                sendPrefixedMessage(sender, "timedpermission.no_permission");
            } else {
                Player p = (Player) sender;
                User user = plugin.luckPerms.getPlayerAdapter(Player.class).getUser(p);
                List<InheritanceNode> nodes = user.getNodes(NodeType.INHERITANCE)
                        .stream()
                        .filter(Node::hasExpiry)
                        .filter(node -> !node.hasExpired())
                        .collect(Collectors.toList());
                if (nodes.size() == 0) {
                    sendPrefixedMessage(sender, "timedpermission.no_permission");
                } else {
                    Instant instant = nodes.get(0).getExpiry();

                    Date date = Date.from(instant);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'o' HH:mm");
                    String formattedDate = formatter.format(date);

                    String msg = getMessage("timedpermission.expires");
                    msg = MessageFormat.format(msg, formattedDate);

                    sender.sendMessage(prefix + msg);
                }
            }
            return true;
        }
        return true;
    }
}
