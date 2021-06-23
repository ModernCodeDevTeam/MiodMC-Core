package pl.dcrft.Managers;

import org.bukkit.ChatColor;
import pl.dcrft.DragonCraftCore;

import java.io.File;
import java.util.*;

import static pl.dcrft.Managers.ConfigManager.getMessagesFile;

public class LanguageManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static String getMessage(String key){
            String message = getMessagesFile().getString(key);
            if(message != null){
                return ChatColor.translateAlternateColorCodes('&', message);
            }
            return "§cError! Unknown string §e" + key + "\n§cCheck if it exists in §emessages.yml§c.";
    }
    public static List<String> getMessageList(String key){
        List<String> message = new ArrayList<>();
        if(message != null){
            for(String s : getMessagesFile().getStringList(key)) {
                message.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            return message;
        }
        return Collections.singletonList("§cError! Unknown string list §e" + key + "\n§cCheck if it exists in §emessages.yml§c.");
    }
    public static void load() {
        File file = new File(plugin.getDataFolder() + File.separator + "messages.yml");
    }
}
