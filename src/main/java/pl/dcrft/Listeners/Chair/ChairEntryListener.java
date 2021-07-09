package pl.dcrft.Listeners.Chair;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.LanguageManager;

public class ChairEntryListener implements Listener {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();
    final String prefix = LanguageManager.getMessage("prefix");
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType() == Material.AIR) {
            Block block = event.getClickedBlock();
            if(Tag.STAIRS.isTagged(block.getType())){
                Stairs stairs = (Stairs) block.getBlockData();
                if(block.getRelative(BlockFace.UP).getType().equals(Material.AIR) && stairs.getShape().equals(Stairs.Shape.STRAIGHT) && stairs.getHalf().equals(Bisected.Half.BOTTOM)) {

                    Player p = event.getPlayer();

                    Location loc = block.getLocation();
                    loc.setX(loc.getBlockX() + 0.5);
                    loc.setZ(loc.getBlockZ() + 0.5);
                    loc.setY(loc.getBlockY() - 0.5);

                    ArmorStand armorStand = (ArmorStand) p.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

                    armorStand.setVisible(false);
                    armorStand.setCustomName("Chair");
                    armorStand.setCustomNameVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setVisible(false);
                    armorStand.setSmall(true);

                    Location ploc = p.getLocation();

                    Vector direction = ploc.getDirection().multiply(-1);
                    ploc.setDirection(direction);

                    p.teleport(ploc);

                    armorStand.addPassenger(p);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            p.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(prefix + LanguageManager.getMessage("chairs.entry")));
                        }
                    }, 2);
                }
            }
        }
    }
}
