package pl.dcrft.Managers;

import org.bukkit.entity.Player;

import static pl.dcrft.Managers.Language.LanguageManager.getMessage;
import static pl.dcrft.Managers.Language.LanguageManager.getMessageList;

public class MessageManager {
    public static void sendMessage(Player p, String key){
        p.sendMessage(getMessage(key));
    }
    public static void sendMessageList(Player p, String key){
        for (final String msg : getMessageList(key)) {
            p.sendMessage(msg);
        }
    }
    public static void sendPrefixedMessage(Player p, String key){
        p.sendMessage(getMessage("prefix") + getMessage(key));
    }
}
