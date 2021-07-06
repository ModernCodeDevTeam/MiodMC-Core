package pl.dcrft.Managers.Statistic;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.LanguageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;

public class StatisticGUIManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static void showStatistics(Player sender, String p) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!StatisticManager.checkPlayer(p)) {
                sendPrefixedMessage(sender, "wrong_player_nickname");
                return;
            }
            HashMap <StatisticType, String> statistics = StatisticManager.getStatistics(p);

            String kills = statistics.get(StatisticType.KILLS);
            String deaths = statistics.get(StatisticType.DEATHS);
            String kdr = statistics.get(StatisticType.KDR);
            String rank = statistics.get(StatisticType.RANK);
            String blocks = statistics.get(StatisticType.BLOCKS);
            String timeplayed = statistics.get(StatisticType.TIMEPLAYED);
            String marry = statistics.get(StatisticType.MARRY);

            String since = statistics.get(StatisticType.SINCE);
            String online = statistics.get(StatisticType.ONLINE);
            String server_online = statistics.get(StatisticType.SERVER_ONLINE);

            Inventory inv = Bukkit.createInventory(null, 54, LanguageManager.getMessage("statistics.title") + p);

            ItemStack green = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
            ItemStack orange = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE, 1);
            ItemStack yellow = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);

            for (int i = 0; i < 9; ++i) {
                inv.setItem(i, green);
            }
            for (int i = 0; i < 9; ++i) {
                inv.setItem(i + 9, white);
            }
            inv.setItem(12, orange);
            inv.setItem(14, orange);

            for (int i = 0; i < 36; ++i) {
                inv.setItem(i + 18, orange);
            }
            inv.setItem(13, yellow);
            inv.setItem(21, yellow);
            inv.setItem(23, yellow);

            inv.setItem(27, yellow);
            inv.setItem(28, yellow);

            inv.setItem(34, yellow);
            inv.setItem(35, yellow);

            inv.setItem(39, yellow);
            inv.setItem(41, yellow);

            inv.setItem(45, yellow);
            inv.setItem(49, yellow);


            ItemStack glowa = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) glowa.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(p)));
            meta.setDisplayName(LanguageManager.getMessage("statistics.head") + " " + p);
            glowa.setItemMeta(meta);
            inv.setItem(4, glowa);

            ItemStack wool;
            ItemMeta meta2;
            if (online.equalsIgnoreCase("teraz")) {
                wool = new ItemStack(Material.LIME_WOOL, 1, (short) 5);
                meta2 = wool.getItemMeta();
                meta2.setDisplayName(LanguageManager.getMessage("statistics.status.online") + "\n" + LanguageManager.getMessage("statistics.status.current_server") + server_online);
            } else {
                wool = new ItemStack(Material.RED_WOOL, 1, (short) 14);
                meta2 = wool.getItemMeta();
                meta2.setDisplayName(LanguageManager.getMessage("statistics.status.offline") + online);
            }
            wool.setItemMeta(meta2);
            inv.setItem(20, wool);

            ItemStack emeraldBlock = new ItemStack(Material.EMERALD);
            ItemMeta meta3 = emeraldBlock.getItemMeta();
            meta3.setDisplayName(LanguageManager.getMessage("statistics.rank") + " " + rank);
            emeraldBlock.setItemMeta(meta3);
            inv.setItem(22, emeraldBlock);
            ItemStack mapa = new ItemStack(Material.MAP);
            ItemMeta meta4 = mapa.getItemMeta();
            if (marry != null && !marry.equalsIgnoreCase("NULL")) {
                meta4.setDisplayName(LanguageManager.getMessage("statistics.marry.title") + " " + marry);
            } else {
                meta4.setDisplayName(LanguageManager.getMessage("statistics.marry.title") + " " + LanguageManager.getMessage("statistics.marry.none"));
            }

            mapa.setItemMeta(meta4);
            inv.setItem(29, mapa);
            ItemStack kilof = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta meta5 = kilof.getItemMeta();
            meta5.setDisplayName(LanguageManager.getMessage("statistics.blocks") + " " + blocks);
            kilof.setItemMeta(meta5);
            inv.setItem(31, kilof);
            ItemStack siekiera = new ItemStack(Material.WOODEN_AXE);
            ItemMeta meta6 = siekiera.getItemMeta();
            List<Punishment> pun = PunishmentManager.get().getPunishments(p, null, true);
            if (pun.isEmpty()) {
                meta6.setDisplayName(LanguageManager.getMessage("statistics.punishments.title") + " " + LanguageManager.getMessage("statistics.punishments.none"));
            } else {
                meta6.setDisplayName(LanguageManager.getMessage("statistics.punishments.title"));
                ArrayList<String> lista = new ArrayList<>();

                for (Punishment punishment : pun) {
                    if (!punishment.getType().toString().equals("WARNING") && !punishment.getType().toString().equals("TEMP_WARNING")) {
                        if (punishment.getType().toString().equals("BAN")) {
                            lista.add(LanguageManager.getMessage("statistics.punishments.ban") + punishment.getReason());
                        } else {
                            Date d;
                            SimpleDateFormat df2;
                            String data;
                            switch (punishment.getType().toString()) {
                                case "TEMP_BAN":
                                    d = new Date(punishment.getEnd());
                                    df2 = new SimpleDateFormat("dd.MM.yyyy 'o' HH:mm");
                                    data = df2.format(d);
                                    lista.add(LanguageManager.getMessage("statistics.punishments.title") + punishment.getReason() + LanguageManager.getMessage("statistics.punishments.expires") + data);
                                    break;
                                case "MUTE":
                                    lista.add(LanguageManager.getMessage("statistics.punishments.mute") + punishment.getReason());
                                    break;
                                case "TEMP_MUTE":
                                    d = new Date(punishment.getEnd());
                                    df2 = new SimpleDateFormat("dd.MM.yyyy 'o' HH:mm");
                                    data = df2.format(d);
                                    lista.add(LanguageManager.getMessage("statistics.punishments.mute") + punishment.getReason() + LanguageManager.getMessage("statistics.punishments.expires") + data);
                                    break;
                                default:
                                    lista.add(LanguageManager.getMessage("statistics.punishments.error"));
                                    break;
                            }
                        }
                    } else {
                        lista.add(LanguageManager.getMessage("statistics.punishments.warning") + punishment.getReason());
                    }

                    meta6.setLore(lista);
                }
            }

            siekiera.setItemMeta(meta6);
            inv.setItem(24, siekiera);
            ItemStack miecz = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta7 = miecz.getItemMeta();
            meta7.setDisplayName(LanguageManager.getMessage("statistics.kills") + " " + kills);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(LanguageManager.getMessage("statistics.deaths") + " " + deaths);
            lore.add(LanguageManager.getMessage("statistics.kdr") + " " + kdr);
            meta7.setLore(lore);
            miecz.setItemMeta(meta7);
            inv.setItem(38, miecz);
            ItemStack jablko = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta meta8 = jablko.getItemMeta();
            meta8.setDisplayName(LanguageManager.getMessage("statistics.since") + " " + since);
            jablko.setItemMeta(meta8);
            inv.setItem(40, jablko);


            ItemStack clock = new ItemStack(Material.CLOCK);
            ItemMeta meta10 = clock.getItemMeta();
            meta10.setDisplayName(LanguageManager.getMessage("statistics.timeplayed") + " " + timeplayed);
            clock.setItemMeta(meta10);
            inv.setItem(33, clock);

            ItemStack wersja = new ItemStack(Material.REDSTONE_TORCH, (short) 1);

            ItemMeta meta9 = wersja.getItemMeta();
            meta9.setDisplayName(LanguageManager.getMessage("statistics.plugin_version")+ " " + plugin.getDescription().getVersion());
            wersja.setItemMeta(meta9);
            inv.setItem(53, wersja);
            Bukkit.getScheduler().runTask(plugin, () -> sender.openInventory(inv));


        });
    }
}
