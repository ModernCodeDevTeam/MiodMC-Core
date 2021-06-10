package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static pl.dcrft.Managers.Language.LanguageManager.getMessage;
import static pl.dcrft.Managers.Language.LanguageManager.getMessageList;

public class MessageManager {
    private static final String prefix = getMessage("prefix");

    public static void sendMessage(Object player, String key){
        if(player instanceof Player) {
            ((Player) player).sendMessage(getMessage(key));
        }
        else if(player instanceof CommandSender){
            ((CommandSender) player).sendMessage(getMessage(key));
        }
    }
    public static void sendMessageList(Object player, String key){
        for (final String msg : getMessageList(key)) {
            if(player instanceof Player) {
                ((Player) player).sendMessage(msg);
            }
            else if(player instanceof CommandSender){
                ((CommandSender) player).sendMessage(msg);
            }
        }
    }
    public static void sendPrefixedMessage(Object player, String key){
        if(player instanceof Player) {
            ((Player) player).sendMessage(getMessage("prefix") + getMessage(key));
        }
        else if(player instanceof CommandSender){
            ((CommandSender) player).sendMessage(getMessage("prefix") + getMessage(key));
        }
    }
    public static void broadcast(String message){
        Bukkit.getServer().broadcastMessage(message);
    }
    public static void broadcastPrefixed(String message){
        Bukkit.getServer().broadcastMessage(prefix + message);
    }
}
