package pl.dcrft.Managers;

import org.bukkit.scheduler.BukkitRunnable;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static pl.dcrft.Utils.Error.ErrorUtil.logError;
import static pl.dcrft.Managers.ConfigManger.getCustomConfig;

public class DatabaseManager {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;

    public static Connection connection;
    public static String host = getCustomConfig().getString("host");
    public static String database = getCustomConfig().getString("database");
    public static String username = getCustomConfig().getString("user");
    public static String password = getCustomConfig().getString("password");
    public static int port = getCustomConfig().getInt("port");
    public static String table = getCustomConfig().getString("table");
    public static String table_bungee = getCustomConfig().getString("table_bungee");

    public static void openConnection() {
                try {
                    if (connection == null || connection.isClosed()) {
                        synchronized(plugin) {
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

    public static void closeConnection() {
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logError(ErrorReason.DATABASE);
                }

            }
        };
        runnable.runTaskAsynchronously(plugin);



    }
}
