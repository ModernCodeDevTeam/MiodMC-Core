package pl.dcrft.Listeners.Anvil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.dcrft.Managers.ConfigManager;

import java.util.Set;

import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;

public class AnvilBreakListener implements Listener {
    private final FileConfiguration data = ConfigManager.getDataFile();
    @EventHandler
    public void onAnvilBreak(BlockBreakEvent e) {
            Block block = e.getBlock();
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
                            data.set("anvils." + i, null);
                            sendPrefixedMessage(e.getPlayer(), "anvils.deleted");
                            ConfigManager.saveData();
                        }
                    }
                }
            }
    }
}
