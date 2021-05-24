package pl.dcrft.Managers;

import org.bukkit.entity.Player;
import pl.dcrft.Managers.LanguageManager.LanguageManager;

import static pl.dcrft.Managers.LanguageManager.LanguageManager.getMessage;

public class MessageManager {
    public static void sendMessage(Player p, String key){
        p.sendMessage(getMessage(key));
    }
    public static void sendPrefixedMessage(Player p, String key){
        p.sendMessage(getMessage("prefix") + getMessage(key));
    }
}
