package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static pl.dcrft.Managers.ConfigManager.getDataFile;
import static pl.dcrft.Managers.DatabaseManager.*;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (!event.getPlayer().hasPermission("panel.adm")) {
            Player p = event.getPlayer();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'o' HH:mm");
            LocalDateTime now = LocalDateTime.now();
            getDataFile().set(p.getName() + ".online", dtf.format(now));
            ConfigManager.saveData();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                openConnection();
                Statement statement;
                try {
                    statement = connection.createStatement();
                    String update = PlaceholderAPI.setPlaceholders(event.getPlayer(), "UPDATE " + table_bungee + " SET online='"+ dtf.format(now) + "', serwer_online='null' WHERE nick = '" + event.getPlayer().getName() + "'");
                    statement.executeUpdate(update);
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logError(ErrorReason.DATABASE);
                }
            });
        }
    }
}
