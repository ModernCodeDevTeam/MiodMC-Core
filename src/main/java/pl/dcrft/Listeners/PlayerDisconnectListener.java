package pl.dcrft.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.dcrft.Managers.SessionManager;


public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for (int i = 0; i < SessionManager.list.size(); i++) {
            SessionManager session = SessionManager.list.get(i);
            if (session.getPlayer().getName().equalsIgnoreCase(p.getName()))
                SessionManager.list.remove(session);
        }
    }
}
