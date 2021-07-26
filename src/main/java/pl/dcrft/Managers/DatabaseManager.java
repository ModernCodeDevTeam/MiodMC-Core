package pl.dcrft.Managers;

import org.bukkit.scheduler.BukkitRunnable;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static Connection connection;
    public static final String host = ConfigManager.getDatabaseFile().getString("host");
    public static final String database = ConfigManager.getDatabaseFile().getString("database");
    public static final String username = ConfigManager.getDatabaseFile().getString("user");
    public static final String password = ConfigManager.getDatabaseFile().getString("password");
    public static final int port = ConfigManager.getDatabaseFile().getInt("port");
    public static final String table = ConfigManager.getDatabaseFile().getString("table");
    public static final String table_bungee = ConfigManager.getDatabaseFile().getString("table_bungee");

    public static void openConnection() {
                try {
                    if (connection == null || connection.isClosed()) {
                        synchronized(plugin) {
                            if (connection == null || connection.isClosed()) {
                                Class.forName("com.mysql.jdbc.Driver");
                                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&socketTimeout=60&loginTimeout=120&threadUsed=true&verifyServerCertificate=false&useSSL=false&testOnBorrow=true&validationQuery='SELECT 1'&validationInterval=60&connect_timeout=60&interactive_timeout=60&wait_timeout=60&useUnicode=yes&character_set_server=utf8mb4&characterEncoding=UTF-8", username, password);
                            }
                        }
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    ErrorUtil.logError(ErrorReason.DATABASE);
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
                    ErrorUtil.logError(ErrorReason.DATABASE);
                }

            }
        };
        runnable.runTaskAsynchronously(plugin);



    }
}
