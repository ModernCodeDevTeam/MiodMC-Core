package pl.dcrft.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.dcrft.Managers.Language.LanguageManager;

public class InvetoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (title.contains(LanguageManager.getMessage("statistics.title"))) {
            e.setCancelled(true);
        }

    }
}
