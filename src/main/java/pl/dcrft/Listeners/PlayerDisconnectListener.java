package pl.dcrft.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.dcrft.Managers.SessionManager;

import static pl.dcrft.Managers.SessionManager.list;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for (int i = 0; i < list.size(); i++) {
            SessionManager session = list.get(i);
            if (session.getPlayer().getName().equalsIgnoreCase(p.getName()))
                list.remove(session);
        }
    }
}
