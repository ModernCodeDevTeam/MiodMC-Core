package pl.dcrft.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.dcrft.Managers.ConfigManager;

import java.util.List;

public class BlockBreakListener implements Listener {
    private FileConfiguration data = ConfigManager.getDataFile();
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getBlock().getType() == Material.ANVIL){
            Location clicked = e.getBlock().getLocation();
            int x = clicked.getBlockX();
            int y = clicked.getBlockY();
            int z = clicked.getBlockZ();
            String world = e.getPlayer().getWorld().getName();
            List<String> an;
        }
    }
}
