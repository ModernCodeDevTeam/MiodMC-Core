package pl.dcrft.Listeners.Anvil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.dcrft.Managers.ConfigManager;

import java.util.Set;

public class AnvilDamageListener implements Listener {
    private FileConfiguration data = ConfigManager.getDataFile();
    @EventHandler
    public void damageAnvil(PlayerInteractEvent e) {
        if (e.hasBlock() && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block.getType() == Material.ANVIL || block.getType() == Material.CHIPPED_ANVIL || block.getType() == Material.DAMAGED_ANVIL) {

                Location clicked = block.getLocation();
                Set<String> anvils = data.getConfigurationSection("anvils").getKeys(false);

                if (anvils != null) {
                    for (String i : anvils) {
                        int x = data.getInt("anvils." + i + ".x");
                        int y = data.getInt("anvils." + i + ".y");
                        int z = data.getInt("anvils." + i + ".z");
                        String world = data.getString("anvils." + i + ".world");
                        Location al = new Location(Bukkit.getWorld(world), x, y, z);
                        if (clicked.equals(al)) {

                            BlockFace facing = ((Directional) block.getBlockData()).getFacing();

                            block.setType(Material.ANVIL);

                            BlockData blockData = block.getBlockData();

                            ((Directional) blockData).setFacing(facing);

                            block.setBlockData(blockData);
                        }
                    }
                }
            }
        }
    }
}
