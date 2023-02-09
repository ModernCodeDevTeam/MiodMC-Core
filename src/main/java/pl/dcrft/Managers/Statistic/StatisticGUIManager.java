package pl.dcrft.Managers.Statistic;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Managers.LanguageManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;

public class StatisticGUIManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static void showStatistics(ServerType serverType, Player sender, String p) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!StatisticManager.checkPlayer(p)) {
                sendPrefixedMessage(sender, "wrong_player_nickname");
                return;
            }
            HashMap<StatisticType, String> statistics = StatisticManager.getStatistics(p);
            String kills,deaths,kdr,blocks,level,money,timeplayed,marry;
            kills = deaths = kdr = blocks = level = money = timeplayed = marry = null;
            switch (serverType){
                case Survival:
                    kills = statistics.get(StatisticType.SURVIVAL_KILLS);
                    deaths = statistics.get(StatisticType.SURVIVAL_DEATHS);
                    kdr = statistics.get(StatisticType.SURVIVAL_KDR);
                    blocks = statistics.get(StatisticType.SURVIVAL_BLOCKS);

                    timeplayed = statistics.get(StatisticType.SURVIVAL_TIMEPLAYED);
                    marry = statistics.get(StatisticType.SURVIVAL_MARRY);
                case SkyBlock:
                    kills = statistics.get(StatisticType.SKYBLOCK_KILLS);
                    deaths = statistics.get(StatisticType.SKYBLOCK_DEATHS);
                    kdr = statistics.get(StatisticType.SKYBLOCK_KDR);
                    level = statistics.get(StatisticType.SKYBLOCK_LEVEL);
                    money = statistics.get(StatisticType.SKYBLOCK_MONEY);

                    timeplayed = statistics.get(StatisticType.SKYBLOCK_TIMEPLAYED);
                    marry = statistics.get(StatisticType.SKYBLOCK_MARRY);
            }

            String rank = statistics.get(StatisticType.RANK);
            String since = statistics.get(StatisticType.SINCE);
            String online = statistics.get(StatisticType.ONLINE);


            try {
                DatabaseManager.openConnection();
                final ItemStack siekiera = new ItemStack(Material.WOODEN_AXE);
                final ItemMeta meta6 = siekiera.getItemMeta();

                final Statement pun = DatabaseManager.connection.createStatement();
                final ResultSet punishments = pun.executeQuery(
                        " SELECT * FROM bm_players" +
                                " LEFT JOIN (SELECT player_id AS p_id, reason AS ban_reason, expires AS ban_expires, created AS ban_created FROM bm_player_bans) AS bans" +
                                " ON bm_players.id = bans.p_id" +
                                " LEFT JOIN (SELECT player_id AS p_id, reason AS warn_reason, expires AS warn_expires, created AS warn_created FROM bm_player_warnings) AS warns" +
                                " ON bm_players.id = warns.p_id" +
                                " LEFT JOIN (SELECT player_id AS p_id, reason AS mute_reason, expires AS mute_expires, created AS mute_created FROM bm_player_mutes) AS mutes" +
                                " ON bm_players.id = mutes.p_id" +
                                " WHERE bm_players.name = '" + p + "'");

                if (punishments.next()) {
                    meta6.displayName(Component.text(LanguageManager.getMessage("statistics.punishments.title")));
                    List<Component> lore = new ArrayList<>();

                    List<String> bans = new ArrayList<>();
                    List<String> warns = new ArrayList<>();
                    List<String> mutes = new ArrayList<>();

                    bans.add(punishments.getString("ban_reason"));
                    warns.add(punishments.getString("warn_reason"));
                    mutes.add(punishments.getString("mute_reason"));

                    while (punishments.next()) {
                        warns.add(punishments.getString("warn_reason"));
                    }

                    if (bans.get(0) != null) {
                        lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.bans")));
                        for (String s : bans) {
                            lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.color") + s));
                        }
                    }
                    if (warns.get(0) != null) {
                        lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.warns")));
                        for (String s : warns) {
                            lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.color") + s));
                        }
                    }
                    if (mutes.get(0) != null) {
                        lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.mutes")));
                        for (String s : mutes) {
                            lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.color") + s));
                        }
                    }
                    if (lore.size() > 0) {
                        meta6.lore(lore);
                    } else {
                        meta6.displayName(Component.text(LanguageManager.getMessage("statistics.punishments.title-none")));
                    }
                }
                siekiera.setItemMeta(meta6);



                ItemStack glowa = new ItemStack(Material.PLAYER_HEAD);

                SkullMeta meta = (SkullMeta) glowa.getItemMeta();
                UUID uuid = plugin.es.getOfflineUser(p).getUUID();
                String pName = p;
                if (uuid != null) {
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                    pName = Bukkit.getOfflinePlayer(uuid).getName();
                } else {
                    meta.setOwner(p);
                }

                meta.setDisplayName(LanguageManager.getMessage("statistics.head") + " " + pName);
                glowa.setItemMeta(meta);



                Inventory inv = Bukkit.createInventory(null, 54, LanguageManager.getMessage("statistics.title") + pName);


                final ItemStack survi = new ItemStack(Material.IRON_PICKAXE);
                final ItemMeta metaSurvi = survi.getItemMeta();
                metaSurvi.displayName(Component.text(LanguageManager.getMessage("statistics.server.survival")));
                survi.setItemMeta(metaSurvi);
                inv.setItem(3, survi);

                final ItemStack sky = new ItemStack(Material.GRASS_BLOCK);
                final ItemMeta metaSky = sky.getItemMeta();
                metaSky.displayName(Component.text(LanguageManager.getMessage("statistics.server.skyblock")));
                sky.setItemMeta(metaSky);
                inv.setItem(5, sky);

                inv.setItem(25, siekiera);

                final ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                final ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

                int[] white = new int[]{10, 11, 15, 16, 28, 34, 37, 38, 40, 42, 43};
                for (int i : white) {
                    inv.setItem(i, whiteGlass);
                }


                inv.setItem(13, glowa);

                ItemStack wool;
                ItemMeta meta2;
                if (online.equalsIgnoreCase("teraz")) {
                    wool = new ItemStack(Material.LIME_DYE, 1, (short) 5);
                    meta2 = wool.getItemMeta();
                    meta2.setDisplayName(LanguageManager.getMessage("statistics.status.online"));
                } else {
                    wool = new ItemStack(Material.RED_DYE, 1, (short) 14);
                    meta2 = wool.getItemMeta();
                    meta2.setDisplayName(LanguageManager.getMessage("statistics.status.offline") + online);
                }
                wool.setItemMeta(meta2);
                inv.setItem(12, wool);

                ItemStack emeraldBlock = new ItemStack(Material.EMERALD);
                ItemMeta meta3 = emeraldBlock.getItemMeta();
                meta3.setDisplayName(LanguageManager.getMessage("statistics.rank") + " " + rank);
                emeraldBlock.setItemMeta(meta3);
                inv.setItem(14, emeraldBlock);
                ItemStack mapa = new ItemStack(Material.MAP);
                ItemMeta meta4 = mapa.getItemMeta();
                if (marry != null && !marry.equalsIgnoreCase("NULL")) {
                    meta4.setDisplayName(LanguageManager.getMessage("statistics.marry.title") + " " + marry);
                } else {
                    meta4.setDisplayName(LanguageManager.getMessage("statistics.marry.title") + " " + LanguageManager.getMessage("statistics.marry.none"));
                }

                mapa.setItemMeta(meta4);
                inv.setItem(23, mapa);

                if(serverType == ServerType.Survival) {
                    ItemStack kilof = new ItemStack(Material.DIAMOND_PICKAXE);
                    ItemMeta meta5 = kilof.getItemMeta();
                    meta5.setDisplayName(LanguageManager.getMessage("statistics.blocks") + " " + blocks);
                    kilof.setItemMeta(meta5);
                    inv.setItem(21, kilof);
                } else if (serverType == ServerType.SkyBlock) {
                    ItemStack emerald = new ItemStack(Material.DIAMOND);
                    ItemMeta meta5 = emerald.getItemMeta();
                    meta5.displayName(Component.text((LanguageManager.getMessage("statistics.level") + level)));
                    emerald.setItemMeta(meta5);
                    inv.setItem(21, emerald);

                    ItemStack kasa = new ItemStack(Material.DRAGON_EGG);
                    ItemMeta metaKasa = kasa.getItemMeta();
                    metaKasa.displayName(Component.text((LanguageManager.getMessage("statistics.money") + money)));
                    kasa.setItemMeta(metaKasa);
                    inv.setItem(32, kasa);

                }

                ItemStack miecz = new ItemStack(Material.IRON_SWORD);
                ItemMeta meta7 = miecz.getItemMeta();
                meta7.setDisplayName(LanguageManager.getMessage("statistics.kills") + " " + kills);
                ArrayList<String> lore = new ArrayList<>();
                lore.add(LanguageManager.getMessage("statistics.deaths") + " " + deaths);
                lore.add(LanguageManager.getMessage("statistics.kdr") + " " + kdr);
                meta7.setLore(lore);
                miecz.setItemMeta(meta7);
                inv.setItem(19, miecz);
                ItemStack jablko = new ItemStack(Material.GOLDEN_APPLE);
                ItemMeta meta8 = jablko.getItemMeta();
                meta8.setDisplayName(LanguageManager.getMessage("statistics.since") + " " + since);
                jablko.setItemMeta(meta8);
                inv.setItem(39, jablko);


                ItemStack clock = new ItemStack(Material.CLOCK);
                ItemMeta meta10 = clock.getItemMeta();
                meta10.setDisplayName(LanguageManager.getMessage("statistics.timeplayed") + " " + timeplayed);
                clock.setItemMeta(meta10);
                inv.setItem(30, clock);

                ItemStack wersja = new ItemStack(Material.REDSTONE_TORCH, (short) 1);

                ItemMeta meta9 = wersja.getItemMeta();
                meta9.setDisplayName(LanguageManager.getMessage("statistics.plugin_version") + " " + plugin.getDescription().getVersion());
                wersja.setItemMeta(meta9);
                inv.setItem(41, wersja);

                for (int i = 0; i < inv.getSize(); i++) {
                    if (inv.getItem(i) == null) inv.setItem(i, blackGlass);
                }

                Bukkit.getScheduler().runTask(plugin, () -> sender.openInventory(inv));
                punishments.close();
                pun.close();


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
