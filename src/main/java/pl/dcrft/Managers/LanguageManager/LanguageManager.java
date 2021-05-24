package pl.dcrft.Managers.LanguageManager;

import org.yaml.snakeyaml.Yaml;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.Error.ErrorReason;

import javax.lang.model.type.ErrorType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.TreeMap;

import static pl.dcrft.Utils.Error.ErrorUtil.logError;

public class LanguageManager {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;
    private static File file;
    private static TreeMap<String, Object> language = new TreeMap<>();
    public static String getMessage(String key){
            Object o = language.get(key);

            if (o != null) {
                return o.toString();
            }

            return null;
    }
    public static void load() {
        file = new File(plugin.getDataFolder() + File.separator + "messages.yml");
    }
    public static void loadFile() {
        try {
            InputStream fileLanguage = new FileInputStream(file);
            HashMap<String, Object> objects = (HashMap<String, Object>) new Yaml().load(fileLanguage);
            if (objects != null) {
                language.putAll(objects);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logError(ErrorReason.MESSAGES);
        }
    }
}
