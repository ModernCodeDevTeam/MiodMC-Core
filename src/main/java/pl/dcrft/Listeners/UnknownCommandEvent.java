package pl.dcrft.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import pl.dcrft.Managers.LanguageManager;

public class UnknownCommandEvent implements Listener {

    private final String unknownMessage =  LanguageManager.getMessage("prefix") + LanguageManager.getMessage("notfound");

    @EventHandler
    public void preProcess(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().substring(1).split(" ")[0];

        if (commandExists(command)) return;
        
        e.setCancelled(true);
        e.getPlayer().sendMessage(unknownMessage);
    }

    @EventHandler
    public void preProcess(ServerCommandEvent e) {
        String command = e.getCommand().split(" ")[0];

        if (commandExists(command)) return;
        
        e.setCancelled(true);
        e.getSender().sendMessage(unknownMessage);
    }

    public boolean commandExists(String cmd) {
        return Bukkit.getHelpMap().getHelpTopic("/" + cmd) != null;
    }
}
