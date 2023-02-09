package pl.dcrft.Utils;

import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseUtil {
    public static void initializeTables(){
        DatabaseManager.openConnection();
        try {
            Statement statement = DatabaseManager.connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + DatabaseManager.table_survival + "` (\n" +
                    "  `nick` text COLLATE utf8mb4_polish_ci NOT NULL,\n" +
                    "  `kille` int(11) NOT NULL,\n" +
                    "  `dedy` int(11) NOT NULL,\n" +
                    "  `kdr` float NOT NULL,\n" +
                    "  `bloki` int(11) NOT NULL,\n" +
                    "  `slub` text COLLATE utf8mb4_polish_ci,\n" +
                    "  `ID` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY\n" +
                    ")  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_polish_ci;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + DatabaseManager.table_skyblock + "` (\n" +
                    "  `nick` text COLLATE utf8mb4_polish_ci NOT NULL,\n" +
                    "  `kille` int(11) NOT NULL,\n" +
                    "  `dedy` int(11) NOT NULL,\n" +
                    "  `kdr` float NOT NULL,\n" +
                    "  `poziom` int(11) NOT NULL,\n" +
                    "  `kasa` int(11) NOT NULL,\n" +
                    "  `slub` text COLLATE utf8mb4_polish_ci,\n" +
                    "  `ID` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY\n" +
                    ")  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_polish_ci;");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.logError(ErrorReason.DATABASE);
        }
    }
}
