package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Managers.LanguageManager;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PlayerQuitListener implements Listener {
    private static DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {

        Player p = event.getPlayer();

        for (String s : ConfigManager.getDataFile().getStringList("players." + p.getName() + ".znajomi")) {
            if(Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s).isOnline()) {
                Bukkit.getPlayer(s).sendMessage(LanguageManager.getMessage("prefix") + MessageFormat.format(LanguageManager.getMessage("friends.leave"), p.getName()));
            }
        }

        if (!event.getPlayer().hasPermission("panel.adm")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'o' HH:mm");
            LocalDateTime now = LocalDateTime.now();
            ConfigManager.getDataFile().set("players." + p.getName() + ".online", dtf.format(now));
            ConfigManager.saveData();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                DatabaseManager.openConnection();
                Statement statement;
                try {
                    statement = DatabaseManager.connection.createStatement();
                    String update = PlaceholderAPI.setPlaceholders(event.getPlayer(), "UPDATE " + DatabaseManager.table_bungee + " SET online='"+ dtf.format(now) + "', serwer_online='null' WHERE nick = '" + event.getPlayer().getName() + "'");
                    statement.executeUpdate(update);
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    ErrorUtil.logError(ErrorReason.DATABASE);
                }
            });
        }
    }
}
