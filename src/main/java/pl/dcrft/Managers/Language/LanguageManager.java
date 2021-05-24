package pl.dcrft.Managers.Language;

import org.yaml.snakeyaml.Yaml;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.ConfigUtil;
import pl.dcrft.Utils.Error.ErrorReason;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static pl.dcrft.Managers.ConfigManger.getMessagesFile;
import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class LanguageManager {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;
    private static File file;
    public static String getMessage(String key){
            String message = getMessagesFile().getString(key);
            if(message != null){
                return message;
            }
            return "§cError! Unknown string §e" + key + "\n§cCheck if it exists in §emessages.yml§c.";
    }
    public static List<String> getMessageList(String key){
        List<String> message = getMessagesFile().getStringList(key);
        if(message != null){
            return message;
        }
        return Collections.singletonList("§cError! Unknown string list §e" + key + "\n§cCheck if it exists in §emessages.yml§c.");
    }
    public static void load() {
        file = new File(plugin.getDataFolder() + File.separator + "messages.yml");
    }
}
