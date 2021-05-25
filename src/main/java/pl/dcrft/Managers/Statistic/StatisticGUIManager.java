package pl.dcrft.Managers.Statistic;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static pl.dcrft.Managers.DatabaseManager.*;
import static pl.dcrft.Managers.DatabaseManager.closeConnection;
import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class StatisticGUIManager {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();
    public static void showStatistics(Player p, String[] args){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                    String kills = StatisticManager.getStatistic(p, StatisticType.KILLS);
                    String deaths = StatisticManager.getStatistic(p, StatisticType.DEATHS);
                    String kdr = StatisticManager.getStatistic(p, StatisticType.DEATHS);
                    String rank = StatisticManager.getStatistic(p, StatisticType.DEATHS);
                    String blocks = StatisticManager.getStatistic(p, StatisticType.DEATHS);
                    String marry = StatisticManager.getStatistic(p, StatisticType.DEATHS);

                    String since = StatisticManager.getStatistic(p, StatisticType.DEATHS);
                    String online = StatisticManager.getStatistic(p, StatisticType.DEATHS);
                    String server_online = StatisticManager.getStatistic(p, StatisticType.DEATHS);

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

                    if (!StatisticManager.checkPlayer(p)) {
                        sendPrefixedMessage(p, "wrong_player_nickname");
                        return;
                    }

                    ItemStack emeraldBlock;
                    ItemMeta meta3;

                    if (online.equalsIgnoreCase("teraz")) {
                        emeraldBlock = new ItemStack(Material.LIME_WOOL, 1, (short) 5);
                        meta3 = emeraldBlock.getItemMeta();
                        meta3.setDisplayName("§6Status §e» §aonline\n§6Aktualnie na serwerze§e » &6" + server_online);
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
                    meta3.setDisplayName("§6Ranga §e» " + rank);
                    emeraldBlock.setItemMeta(meta3);
                    inv.setItem(24, emeraldBlock);
                    ItemStack mapa = new ItemStack(Material.MAP);
                    ItemMeta meta4 = mapa.getItemMeta();
                    if (marry != null && !marry.equalsIgnoreCase("NULL")) {
                        meta4.setDisplayName("§6Ślub §e» §e" + marry);
                    } else {
                        meta4.setDisplayName("§6Ślub §e» §ebrak");
                    }

                    mapa.setItemMeta(meta4);
                    inv.setItem(27, mapa);
                    ItemStack kilof = new ItemStack(Material.DIAMOND_PICKAXE);
                    ItemMeta meta5 = kilof.getItemMeta();
                    meta5.setDisplayName("§6Wykopane bloki §e» §e" + blocks);
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

                        for (Punishment punishment : pun) {
                            String typ;
                            if (punishment.getType().toString() != "WARNING" && punishment.getType().toString() != "TEMP_WARNING") {
                                if (punishment.getType().toString() == "BAN") {
                                    typ = "§6Ban";
                                    lista.add("§6Ban" + " §e» §c" + punishment.getReason());
                                } else {
                                    Date d;
                                    SimpleDateFormat df2;
                                    String data;
                                    if (punishment.getType().toString() == "TEMP_BAN") {
                                        typ = "§6Ban";
                                        d = new Date(punishment.getEnd());
                                        df2 = new SimpleDateFormat("dd.MM.YYYY 'o' HH:mm");
                                        data = df2.format(d);
                                        lista.add("§6Ban" + " §e» §c" + punishment.getReason() + ", §6wygasa: §e" + data);
                                    } else if (punishment.getType().toString() == "MUTE") {
                                        typ = "§6Wyciszenie";
                                        lista.add("§6Wyciszenie" + " §e» §c" + punishment.getReason());
                                    } else if (punishment.getType().toString() == "TEMP_MUTE") {
                                        typ = "§6Wyciszenie";
                                        d = new Date(punishment.getEnd());
                                        df2 = new SimpleDateFormat("dd.MM.YYYY 'o' HH:mm");
                                        data = df2.format(d);
                                        lista.add("§6Wyciszenie" + " §e» §c" + punishment.getReason() + ", §6wygasa: §e" + data);
                                    } else {
                                        lista.add("§cbłąd");
                                    }
                                }
                            } else {
                                typ = "§6Ostrzeżenie";
                                lista.add("§6Ostrzeżenie" + " §e» §c" + punishment.getReason());
                            }

                            meta6.setLore(lista);
                        }
                    }

                    siekiera.setItemMeta(meta6);
                    inv.setItem(35, siekiera);
                    ItemStack miecz = new ItemStack(Material.IRON_SWORD);
                    ItemMeta meta7 = miecz.getItemMeta();
                    meta7.setDisplayName("§6Zabójstwa §e» " + kills);
                    List<String> lore = new ArrayList();
                    lore.add("§6Śmierci §e» " + deaths);
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


        });
    }
}
