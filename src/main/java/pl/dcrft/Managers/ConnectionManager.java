package pl.dcrft.Managers;

import org.bukkit.scheduler.BukkitRunnable;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static pl.dcrft.Utils.Error.ErrorUtil.logError;
import static pl.dcrft.Managers.ConfigManger.getCustomConfig;

public class ConnectionManager {
    public static DragonCraftCore plugin;

    public static Connection connection;
    public static String host = getCustomConfig().getString("host");
    public static String database = getCustomConfig().getString("baza");
    public static String username = getCustomConfig().getString("user");
    public static String password = getCustomConfig().getString("haslo");
    public static int port = getCustomConfig().getInt("port");
    public static String tabela = getCustomConfig().getString("tabela");

    public static void openConnection() {
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                try {
                    if (connection == null || connection.isClosed()) {
                        synchronized(this) {
                            if (connection == null || connection.isClosed()) {
                                Class.forName("com.mysql.jdbc.Driver");
                                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", username, password);
                            }
                        }
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    logError(ErrorReason.DATABASE);
                    e.printStackTrace();
                }

            }
        };
        runnable.runTaskAsynchronously(plugin);



    }

    public static void closeConnection() {
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logError(ErrorReason.DATABASE);
                    e.printStackTrace();
                }

            }
        };
        runnable.runTaskAsynchronously(plugin);



    }
}
