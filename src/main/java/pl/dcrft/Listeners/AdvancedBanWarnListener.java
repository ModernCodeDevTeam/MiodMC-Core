package pl.dcrft.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;


//this is such a garbage, dont look here >;c
public class AdvancedBanWarnListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/warn")) {
            e.setCancelled(true);
            String[] args = e.getMessage().split(" ");
            Player p = e.getPlayer();
            if(!e.getPlayer().hasPermission("ab.warn.temp")) {
                sendPrefixedMessage(e.getPlayer(), "notfound");
            }
            else if (args.length <3) {
                sendPrefixedMessage(p, "advancedban_warn_usage");
            }
            else {
                final StringBuilder sb = new StringBuilder();
                for (int k = 2; k < args.length; ++k) {
                    sb.append(args[k]).append(" ");
                }
                final String allArgs = sb.toString().trim();
                p.chat("/tempwarn " + args[1] + " #1 " + allArgs);
            }
        }
    }
}
