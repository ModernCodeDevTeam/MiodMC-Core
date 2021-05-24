package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static pl.dcrft.Managers.ConfigManger.getDataFile;
import static pl.dcrft.Managers.DataManager.saveData;
import static pl.dcrft.Managers.DatabaseManager.*;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) throws SQLException, ClassNotFoundException {
        if (!event.getPlayer().hasPermission("pt.adm")) {
            Player p = event.getPlayer();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.YYYY 'o' HH:mm");
            LocalDateTime now = LocalDateTime.now();
            getDataFile().set(p.getName() + ".online", dtf.format(now));
            saveData();
            openConnection();
            Statement statement = connection.createStatement();
            String update = PlaceholderAPI.setPlaceholders(event.getPlayer(), "UPDATE " + table_bungee + " SET online='"+ dtf.format(now) + "', serwer_online='null' WHERE nick = '" + event.getPlayer().getName() + "'");
            statement.executeUpdate(update);
            statement.close();
        }
    }
}
