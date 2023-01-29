package pl.dcrft.Managers;

import com.earth2me.essentials.Kits;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.dcrft.DragonCraftCore;

import static pl.dcrft.DragonCraftCore.es;

public class KitsManager {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static String[] getKits(Player p) throws Exception {
        User user = es.getUser(p);
        Kits kits = es.getKits();
        String input = kits.listKits(es, user);
        String[] kitsList = input.split(" ");
        return kitsList;
    }
    public static void openGui(Player p) {
        try {

            String[] kitsList = getKits(p);
            int size;
            if ((kitsList.length % 9) > 0) {
                size = 9*(kitsList.length / 9) + 9;
            } else {
                size = 9*(kitsList.length / 9);
            }
            Inventory inv = Bukkit.createInventory(null, size,  LanguageManager.getMessage("prefix") + LanguageManager.getMessage("kits-title"));
            int i = 0;
            for(String s : kitsList){
                s = s.toLowerCase().replace("§m", "").replace("§r", "");
                Material material = Material.CHEST;
                if(plugin.getConfig().getString("kits." + s + ".item") != null) material = Material.getMaterial(plugin.getConfig().getString("kits." + s + ".item"));
                ItemStack is = new ItemStack(material);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§6§n" + s);
                if(plugin.getConfig().getString("kits." + s + ".name") != null) im.setDisplayName(plugin.getConfig().getString("kits." + s + ".name"));
                is.setItemMeta(im);
                inv.setItem(i, is);
                i++;
            }
            p.openInventory(inv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
