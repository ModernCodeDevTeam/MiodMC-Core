package pl.dcrft.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.dcrft.Managers.KitsManager;
import pl.dcrft.Managers.LanguageManager;
import pl.dcrft.Managers.Statistic.ServerType;
import pl.dcrft.Managers.Statistic.StatisticGUIManager;

public class InventoryClickListener implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (title.contains(LanguageManager.getMessage("statistics.title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                switch (e.getCurrentItem().getType()) {
                    case IRON_PICKAXE:
                        StatisticGUIManager.showStatistics(ServerType.Survival, (Player) e.getWhoClicked(), e.getWhoClicked().getName());
                        return;
                    case GRASS_BLOCK:
                        StatisticGUIManager.showStatistics(ServerType.SkyBlock, (Player) e.getWhoClicked(), e.getWhoClicked().getName());
                }
            }
        } else if (title.contains(LanguageManager.getMessage("kits-title"))) {
            e.setCancelled(true);

            final Player p = (Player) e.getWhoClicked();
            final Inventory inv = e.getClickedInventory();
            final ItemStack is = e.getCurrentItem();

            if (is != null && is.getType() != null && is.getType() != Material.AIR) {
                String[] kits;
                try {
                    kits = KitsManager.getKits(p);
                    p.chat("/ekit " + kits[e.getSlot()].replace("§m", "").replace("§r", ""));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                inv.close();
            }


        }

    }
}
