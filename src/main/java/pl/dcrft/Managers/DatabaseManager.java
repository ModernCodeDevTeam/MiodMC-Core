package pl.dcrft.Managers;

import org.bukkit.scheduler.BukkitRunnable;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static pl.dcrft.Utils.ErrorUtils.ErrorUtil.logError;
import static pl.dcrft.Managers.ConfigManager.getDatabaseFile;

public class DatabaseManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static Connection connection;
    public static final String host = getDatabaseFile().getString("host");
    public static final String database = getDatabaseFile().getString("database");
    public static final String username = getDatabaseFile().getString("user");
    public static final String password = getDatabaseFile().getString("password");
    public static final int port = getDatabaseFile().getInt("port");
    public static final String table = getDatabaseFile().getString("table");
    public static final String table_bungee = getDatabaseFile().getString("table_bungee");

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
