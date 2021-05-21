package pl.dcrft.Managers;

import org.bukkit.configuration.file.FileConfiguration;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;

import java.io.File;

import static pl.dcrft.Managers.ConfigManger.dataFile;
import static pl.dcrft.Managers.ConfigManger.dataFileFile;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class DataManager {
    private static DragonCraftCore plugin;
    public static void saveData() {
        try {
            dataFile.save(dataFileFile);

        } catch (Exception e) {
            logError(ErrorReason.DATA);
            e.printStackTrace();
        }
    }
}
