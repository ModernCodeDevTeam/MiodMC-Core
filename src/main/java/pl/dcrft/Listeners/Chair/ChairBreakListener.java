package pl.dcrft.Listeners.Chair;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChairBreakListener implements Listener {
    @EventHandler
    public void onChairBreak(BlockBreakEvent e) {
        if(e.getPlayer().getVehicle() != null
                && e.getPlayer().getVehicle().getType() == EntityType.ARMOR_STAND
                && e.getPlayer().getVehicle().getName().equals("Chair")) {
            e.getPlayer().getVehicle().remove();
        }
    }
}
