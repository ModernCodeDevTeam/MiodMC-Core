package pl.dcrft.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.dcrft.DragonCraftCore;

public class CommandPreprocessListener implements Listener {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().length() > 1) {
            String command = e.getMessage().substring(1);
            command = command.split(" ")[0];

            String aliasResult = plugin.getConfig().getString("aliases." + command);
            if (aliasResult != null) {
                String userArguments = e.getMessage().substring(command.length() + 1);
                e.setMessage(e.getMessage().substring(0, 1) + aliasResult + userArguments);
            }
        }
    }
}
