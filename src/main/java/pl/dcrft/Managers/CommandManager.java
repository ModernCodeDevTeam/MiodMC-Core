package pl.dcrft.Managers;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Panel.PanelManager;
import pl.dcrft.Managers.Panel.PanelType;
import pl.dcrft.Managers.Statistic.StatisticGUIManager;
import pl.dcrft.Utils.ConfigUtil;
import pl.dcrft.Utils.GroupUtil;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor {
    private static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public final ArrayList<SessionManager> list = new ArrayList<>();
    final String prefix = LanguageManager.getMessage("prefix");

    public boolean onCommand(final @NotNull CommandSender sender, final Command cmd, final @NotNull String label, final String[] args) {
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
                MessageManager.sendMessageList(p, "friends.help.contents");
                return false;
            }
            if (args[0].equalsIgnoreCase("lista") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("info")) {
                MessageManager.sendPrefixedMessage(p, "friends.list.title");
                List<String> znajomi = ConfigManager.getDataFile().getStringList("players." + p.getName() + ".znajomi");
                if (znajomi.size() == 0) {
                    MessageManager.sendMessage(p, "friends.list.none");
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
                if (args.length == 1 || Bukkit.getPlayer(args[1]) == null || !Bukkit.getPlayer(args[1]).isOnline()) {
                    MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                    return true;
                }
                else if (args[1].equalsIgnoreCase(sender.getName())) {
                    MessageManager.sendPrefixedMessage(p, "friends.add.self");
                    return true;
                }
                else if (plugin.getConfig().getStringList("staff").contains(Bukkit.getPlayer(args[1]).getName())) {
                    MessageManager.sendPrefixedMessage(p, "friends.add.staff");
                    return true;
                } else {
                    Player o = Bukkit.getPlayer(args[1]);
                    List<String> znajomip = ConfigManager.getDataFile().getStringList("players." + sender.getName() + ".znajomi");
                    if (znajomip.contains(o.getName())) {
                        MessageManager.sendPrefixedMessage(p, "friends.add.already_friend");
                        return true;
                    }
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".znajprosba." + Bukkit.getOfflinePlayer(args[1]).getName(), true);
                    ConfigManager.saveData();
                    o.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.notification.title"), p.getName()));
                    o.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.notification.accept"), p.getName()));
                    o.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.notification.cancel"), p.getName()));
                    sender.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("friends.add.invited"), Bukkit.getOfflinePlayer(args[1]).getName()));
                    return true;
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
                MessageManager.sendMessageList(p, "friends.help.contents");
                return false;
            }
        }


        if (cmd.getName().equalsIgnoreCase("slub")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            if (Bukkit.getPlayer(args[0]) == null || !Bukkit.getPlayer(args[0]).isOnline()) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            Player other = Bukkit.getPlayer(args[0]);
            if (plugin.getConfig().getStringList("staff").contains(Bukkit.getPlayer(args[0]).getName())) {
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
            if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.no_ivite_send");
                return false;
            } else {
                if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + "slub") != null) {
                    MessageManager.sendPrefixedMessage(p, "marry.target_already");
                    return false;
                }

                ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("marry_item.material")));
                item.setAmount(plugin.getConfig().getInt("marry_item.amount"));
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(plugin.getConfig().getString("marry_item.name"));
                itemMeta.setLore(plugin.getConfig().getStringList("marry_item.lore"));
                itemMeta.addEnchant(Enchantment.getByName(plugin.getConfig().getString("marry_item.enchantment.enchantment")), plugin.getConfig().getInt("marry_item.enchantment.level"), true);
                item.setItemMeta(itemMeta);

                if (!p.getInventory().containsAtLeast(item, plugin.getConfig().getInt("marry_item.amount"))) {
                    MessageManager.sendPrefixedMessage(p, "marry.send.accept.missing_item");
                    return false;
                } else {
                    p.getInventory().removeItem(item);
                    if (Bukkit.getPlayer(args[0]).isOnline()) {
                        MessageManager.sendPrefixedMessage(Bukkit.getPlayer(args[0]), "marry.send.accept.accepted");
                    }
                    MessageManager.sendPrefixedMessage(p, "marry.send.accept.accepted");
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slub", Bukkit.getOfflinePlayer(args[0]).getName());
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", p.getName());
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", null);
                    ConfigManager.saveData();
                    DatabaseManager.openConnection();
                    try {
                        Statement statement = DatabaseManager.connection.createStatement();
                        String updatep = "UPDATE `" + DatabaseManager.table + "` SET slub = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "' WHERE nick = '" + p.getName() + "'";
                        String updateo = "UPDATE `" + DatabaseManager.table + "` SET slub = '" + p.getName() + "' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                        statement.executeUpdate(updatep);
                        statement.executeUpdate(updateo);
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    plugin.getServer().broadcastMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.send.accept.broadcast"), p.getName(), Bukkit.getOfflinePlayer(args[0]).getName()));
                    return false;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("sodrzuc")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
            } else if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.no_ivite_send");
            } else {
                MessageManager.sendPrefixedMessage(p, "marry.send.reject.rejected");
                if (Bukkit.getPlayer(args[0]).isOnline()) {
                    MessageManager.sendPrefixedMessage(Bukkit.getPlayer(args[0]), "marry.send.reject.rejected");
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
            } else if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub") == null || !ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub").equalsIgnoreCase(p.getName()) || !ConfigManager.getDataFile().getString("players." + p.getName() + ".slub").equalsIgnoreCase(Bukkit.getOfflinePlayer(args[0]).getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.not_with_target");
            } else {
                ConfigManager.getDataFile().set("players." + p.getName() + ".slub", null);
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", null);
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", null);
                ConfigManager.saveData();
                DatabaseManager.openConnection();
                try {
                    Statement statement = DatabaseManager.connection.createStatement();
                    String updatep = "UPDATE `" + DatabaseManager.table + "` SET slub = 'NULL' WHERE nick = '" + p.getName() + "'";
                    String updateo = "UPDATE `" + DatabaseManager.table + "` SET slub = 'NULL' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                    statement.executeUpdate(updatep);
                    statement.executeUpdate(updateo);
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                plugin.getServer().broadcastMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.send.reject.broadcast"), p.getName(), Bukkit.getOfflinePlayer(args[0]).getName()));

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
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.vip"));
            } else {
                MessageManager.sendMessageList(p, "ranks.vip");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("svip")) {
            Player p = (Player) sender;
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.svip"));
            } else {
                MessageManager.sendMessageList(p, "ranks.svip");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("mvip")) {
            Player p = (Player) sender;
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.mvip"));
            } else {
                MessageManager.sendMessageList(p, "ranks.mvip");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("evip")) {
            Player p = (Player) sender;
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
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
                Bukkit.getServer().broadcastMessage(prefix + LanguageManager.getMessage("chat.cleared"));
                return true;
            }
            if (!sender.hasPermission("cc.adm")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
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
                MessageManager.sendPrefixedMessage(sender, "notfound");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("dcc")) {
            if (args.length == 0) {
                if (sender.hasPermission("dcc.adm")) {
                    sender.sendMessage("§e§lDragon§6§lCraft§b§lCore " + plugin.getDescription().getVersion());
                    MessageManager.sendMessageList(sender, "pluginhelp.contents");
                } else {
                    MessageManager.sendPrefixedMessage(sender, "notfound");
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
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.kick_msg") + " " + kick_msg);
                    sender.sendMessage(plugin.getConfig().getString("afk.kick_warn_delay") + " " + kick_warn_delay);
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.kick_warn_msg") + " " + kick_warn_msg);
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.kick_warn_msg_afk") + " " + kick_warn_msg_afk);
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.notafkmsg") + " " + notafkmsg);
                    sender.sendMessage(plugin.getConfig().getString("afk.kick_delay") + " " + kick_delay);
                    sender.sendMessage(plugin.getConfig().getString("afk.sound_on_get_warn") + " " + sound_on_get_warn);
                    sender.sendMessage(plugin.getConfig().getString("afk.sound_on_notafk") + " " + sound_on_notafk);
                } else if (sub.equalsIgnoreCase("anvil")) {
                    if (!(sender instanceof Player)) {
                        MessageManager.sendPrefixedMessage(sender, "console_error");
                    } else {
                        Player p = (Player) sender;
                        if (p.getTargetBlock(null, 1) != null) {
                            Block block = p.getTargetBlock(null, 1);
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
                                            MessageManager.sendPrefixedMessage(p, "anvils.deleted");
                                            ConfigManager.saveData();
                                            return true;
                                        }
                                        if (Integer.parseInt(i) >= max) {
                                            max = Integer.parseInt(i) + 1;
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
                                MessageManager.sendPrefixedMessage(p, "anvils.created");

                                ConfigManager.saveData();
                            } else {
                                MessageManager.sendPrefixedMessage(p, "anvils.not_an_anvil");
                            }

                        } else {
                            MessageManager.sendPrefixedMessage(p, "anvils.not_an_anvil");
                        }
                    }
                } else if (sub.equalsIgnoreCase("block")) {
                    if (args.length < 2) {
                        MessageManager.sendPrefixedMessage(sender, "block.usage");
                    } else {
                        String toblock = args[1].replace(":", "%colon%");
                        if (ConfigManager.getDisabledFile().get(toblock) != null) {
                            MessageManager.sendPrefixedMessage(sender, "block.already");
                        } else {
                            if (args.length == 2) {
                                ConfigManager.getDisabledFile().set(toblock + ".Message", LanguageManager.getMessage("prefix") + LanguageManager.getMessage("notfound"));
                                ConfigManager.saveDisabledFile();
                                MessageManager.sendPrefixedMessage(sender, "block.blocked");
                            }
                            else if (args.length > 2) {

                                final StringBuilder sb = new StringBuilder();
                                for (int k = 2; k < args.length; ++k) {
                                    sb.append(args[k]).append(" ");
                                }
                                final String message = sb.toString().trim();

                                ConfigManager.getDisabledFile().set(toblock + ".Message", message);
                                ConfigManager.saveDisabledFile();
                                MessageManager.sendPrefixedMessage(sender, "block.blocked");
                            }
                        }
                    }
                } else if (sub.equalsIgnoreCase("unblock")) {
                    if (args.length < 2) {
                        MessageManager.sendPrefixedMessage(sender, "unblock.usage");
                    } else {
                        String tounblock = args[1].replace(":", "%colon%");
                        if (ConfigManager.getDisabledFile().get(tounblock) == null) {
                            MessageManager.sendPrefixedMessage(sender, "unblock.notfound");
                        } else {
                                ConfigManager.getDisabledFile().set(tounblock, null);
                                ConfigManager.saveDisabledFile();
                                MessageManager.sendPrefixedMessage(sender, "unblock.unblocked");
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
        }   else if (cmd.getName().equalsIgnoreCase("przerwa")) {
            if (sender.hasPermission("r.adm")) {
                if (args.length == 0) {
                    MaintenanceManager.maintenanceStart();
                } else {
                    if (!args[0].chars().allMatch(Character::isDigit) || Integer.parseInt(args[0]) < 1) {
                        MessageManager.sendPrefixedMessage(sender, "maintenance.wrong_value");
                    } else {
                        MaintenanceManager.maintenanceStart(Integer.parseInt(args[0]));
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
                        PanelManager.updatePanel(p, PanelType.ADMIN);
                    } else if (sender.hasPermission("panel.mod")) {
                        PanelManager.updatePanel(p, PanelType.MOD);
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
                        PanelManager.updatePanel(p, PanelType.ADMIN);
                    } else if (p.hasPermission("panel.mod")) {
                        PanelManager.updatePanel(p, PanelType.MOD);
                    }

                    ConfigManager.saveData();
                    return true;
                } else if (!ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".adminchat")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".adminchat", true);
                    sender.sendMessage(LanguageManager.getMessage("staffchat.adminchat.title") + LanguageManager.getMessage("staffchat.modchat.spacer") + LanguageManager.getMessage("staffchat.enabled"));
                    if (p.hasPermission("panel.adm")) {
                        PanelManager.updatePanel(p, PanelType.ADMIN);
                    } else if (p.hasPermission("panel.mod")) {
                        PanelManager.updatePanel(p, PanelType.MOD);
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
                    PanelManager.updatePanel(p, PanelType.ADMIN);
                } else if (p.hasPermission("panel.mod")) {
                    PanelManager.updatePanel(p, PanelType.MOD);
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
                    MessageManager.sendPrefixedMessage(sender, "staffchat.stream.enabled");
                    PanelManager.hidePanel(p);
                    ConfigManager.saveData();
                    return true;
                }
                if (ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".stream")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".stream", false);
                    MessageManager.sendPrefixedMessage(sender, "staffchat.stream.disabled");

                    if (p.hasPermission("panel.adm")) {
                        PanelManager.updatePanel(p, PanelType.ADMIN);
                    } else if (p.hasPermission("panel.mod")) {
                        PanelManager.updatePanel(p, PanelType.MOD);
                    }

                    ConfigManager.saveData();
                    return true;
                } else if (!ConfigManager.getDataFile().getBoolean("players." + sender.getName() + ".stream")) {
                    ConfigManager.getDataFile().set("players." + sender.getName() + ".stream", true);
                    MessageManager.sendPrefixedMessage(sender, "staffchat.stream.enabled");
                    PanelManager.hidePanel(p);
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
                MessageManager.sendPrefixedMessage(sender, "checkwarn.usage");
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
                    MessageManager.sendPrefixedMessage(sender, "wrong_player_nickname");
                } else {
                    StatisticGUIManager.showStatistics(p, args[0]);
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
                Bukkit.getServer().broadcastMessage(MessageFormat.format(LanguageManager.getMessage("shopbroadcast.purchase"), args[0], allArgs));
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
                Bukkit.getServer().broadcastMessage(MessageFormat.format(LanguageManager.getMessage("shopbroadcast.donate"), args[0], allArgs));
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
                    MessageManager.sendPrefixedMessage(sender, "aliases.help.header");
                    MessageManager.sendMessageList(sender, "aliases.help.contents");
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
                                MessageManager.sendPrefixedMessage(sender, "aliases.add.exists");
                            }
                        } else {
                            MessageManager.sendPrefixedMessage(sender, "aliases.add.usage");
                        }
                    } else if (subCommand.equals("usun")) {
                        if (args.length > 1) {
                            if (map.get(args[1]) == null) {
                                MessageManager.sendPrefixedMessage(sender, "aliases.delete.notfound");
                            } else {
                                plugin.getConfig().set("aliases." + args[1], null);
                                plugin.saveConfig();
                                MessageManager.sendPrefixedMessage(sender, "aliases.delete.deleted");
                            }
                        } else {
                            MessageManager.sendPrefixedMessage(sender, "aliases.delete.usage");
                        }
                    } else if (subCommand.equals("lista")) {
                        MessageManager.sendPrefixedMessage(sender, "aliases.help.header");
                        for (String key : map.keySet()) {
                            String value = map.get(key).toString();
                            sender.sendMessage(
                                    LanguageManager.getMessage("aliases.list.spacer") +
                                            " " + LanguageManager.getMessage("aliases.list.from") +
                                            key +
                                            " " + LanguageManager.getMessage("aliases.list.spacer") +
                                            " " + LanguageManager.getMessage("aliases.list.to") +
                                            value);
                        }
                    }
                } else {
                    MessageManager.sendPrefixedMessage(sender, "aliases.help.header");
                    MessageManager.sendMessageList(sender, "aliases.help.contents");
                }
            } else {
                MessageManager.sendPrefixedMessage(sender, "notfound");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("+")) {
            if (!sender.hasPermission(plugin.getConfig().getString("timedpermission"))) {
                MessageManager.sendPrefixedMessage(sender, "timedpermission.no_permission");
            } else {
                Player p = (Player) sender;
                User user = plugin.luckPerms.getPlayerAdapter(Player.class).getUser(p);
                List<InheritanceNode> nodes = user.getNodes(NodeType.INHERITANCE)
                        .stream()
                        .filter(Node::hasExpiry)
                        .filter(node -> !node.hasExpired())
                        .collect(Collectors.toList());
                if (nodes.size() == 0) {
                    MessageManager.sendPrefixedMessage(sender, "timedpermission.no_permission");
                } else {
                    Instant instant = nodes.get(0).getExpiry();

                    Date date = Date.from(instant);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'o' HH:mm");
                    String formattedDate = formatter.format(date);

                    String msg = LanguageManager.getMessage("timedpermission.expires");
                    msg = MessageFormat.format(msg, formattedDate);

                    sender.sendMessage(prefix + msg);
                }
            }
            return true;
        }
        //TODO
        /*
        else if (cmd.getName().equalsIgnoreCase("vote")) {
            if (!(sender instanceof Player)) {
                MessageManager.sendPrefixedMessage(sender, "console_error");
            } else {
                Player p = (Player) sender;
                if (ConfigManager.getDataFile().get("players." + sender.getName() + ".vote") == null) {
                    try {

                        JsonObject json = URLUtil.queryJson("https://mcpc.pl/api/server/checkvote/" + plugin.getConfig().getString("vote_ip") + "/" + p.getAddress().getHostString());
                        Bukkit.getServer().getLogger().info("https://mcpc.pl/api/server/checkvote/" + plugin.getConfig().getString("vote_ip") + "/" + p.getAddress().getHostString());
                        Bukkit.getServer().getLogger().info(json.getAsString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    MessageManager.sendPrefixedMessage(sender, "vote.already");
                }
            }
        }*/
        return true;
    }
}
