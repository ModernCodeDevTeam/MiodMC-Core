package pl.dcrft.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.LanguageManager;

public class PlayerDeathListener implements Listener {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player p = event.getEntity();
        if(!(p.getKiller() instanceof Player)){
            return;
        }
        else {
            double d = Math.random();
            if(d<=plugin.getConfig().getInt("death_head_chance")){

                ItemStack glowa = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) glowa.getItemMeta();
                meta.setOwningPlayer(p);
                meta.setDisplayName(LanguageManager.getMessage("death_head") + p.getName());
                glowa.setItemMeta(meta);

                Location loc = p.getLocation();
                World world = event.getEntity().getWorld();

                world.dropItemNaturally(loc, glowa);
            }

        }
    }
}
