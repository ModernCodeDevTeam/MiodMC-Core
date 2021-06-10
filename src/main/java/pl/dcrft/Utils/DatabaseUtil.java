package pl.dcrft.Utils;

import pl.dcrft.Utils.Error.ErrorReason;

import java.sql.SQLException;

import static pl.dcrft.Managers.DatabaseManager.connection;
import static pl.dcrft.Managers.DatabaseManager.openConnection;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class DatabaseUtil {
    public static void initializeTable(String table){
        openConnection();
        try {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (\n" +
                    "  `nick` text COLLATE utf8mb4_polish_ci NOT NULL,\n" +
                    "  `kille` int(11) NOT NULL,\n" +
                    "  `dedy` int(11) NOT NULL,\n" +
                    "  `kdr` float NOT NULL,\n" +
                    "  `ranga` text COLLATE utf8mb4_polish_ci NOT NULL,\n" +
                    "  `bloki` int(11) NOT NULL,\n" +
                    "  `slub` text COLLATE utf8mb4_polish_ci,\n" +
                    "  `ID` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY\n" +
                    ")  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_polish_ci;");
        } catch (SQLException e) {
            e.printStackTrace();
            logError(ErrorReason.DATABASE);
        }
    }
}
