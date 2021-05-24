package pl.dcrft.Utils;

import pl.dcrft.DragonCraftCore;

import static pl.dcrft.Managers.ConnectionManager.openConnection;

public class DatabaseUtil {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();
    public static void initializeTable(String table){
        openConnection();
    }
}
