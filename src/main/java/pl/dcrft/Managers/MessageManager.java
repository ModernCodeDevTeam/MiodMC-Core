package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.dcrft.Utils.ColorUtil;


public class MessageManager {
    private static final String prefix = LanguageManager.getMessage("prefix");

    public static void sendMessage(Object player, String key){
        if(player instanceof Player) {
            ((Player) player).sendMessage(ColorUtil.colorize(LanguageManager.getMessage(key)));
        }
        else if(player instanceof CommandSender){
            ((CommandSender) player).sendMessage(ColorUtil.colorize(LanguageManager.getMessage(key)));
        }
    }
    public static void sendMessageList(Object player, String key){
        for (final String msg : LanguageManager.getMessageList(key)) {
            if(player instanceof Player) {
                ((Player) player).sendMessage(ColorUtil.colorize(msg));
            }
            else if(player instanceof CommandSender){
                ((CommandSender) player).sendMessage(ColorUtil.colorize(msg));
            }
        }
    }
    public static void sendPrefixedMessage(Object player, String key){
        if(player instanceof Player) {
            ((Player) player).sendMessage(ColorUtil.colorize(LanguageManager.getMessage("prefix") + LanguageManager.getMessage(key)));
        }
        else if(player instanceof CommandSender){
            ((CommandSender) player).sendMessage(ColorUtil.colorize(LanguageManager.getMessage("prefix") + LanguageManager.getMessage(key)));
        }
    }
    public static void broadcast(String message){
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(ColorUtil.colorize(message));
        }
    }
    public static void broadcastPrefixed(String message){
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(ColorUtil.colorize(prefix + message));
        }
    }
}
