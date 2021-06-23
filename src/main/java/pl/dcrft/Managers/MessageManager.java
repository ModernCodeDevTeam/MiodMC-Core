package pl.dcrft.Managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.command.MessageCommandSender;
import org.bukkit.entity.Player;
import pl.dcrft.Utils.ColorUtil;

import static pl.dcrft.Managers.LanguageManager.getMessage;
import static pl.dcrft.Managers.LanguageManager.getMessageList;

public class MessageManager {
    private static final String prefix = getMessage("prefix");

    public static void sendMessage(Object player, String key){
        if(player instanceof Player) {
            ((Player) player).sendMessage(ColorUtil.colorize(getMessage(key)));
        }
        else if(player instanceof CommandSender){
            ((CommandSender) player).sendMessage(ColorUtil.colorize(getMessage(key)));
        }
    }
    public static void sendMessageList(Object player, String key){
        for (final String msg : getMessageList(key)) {
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
            ((Player) player).sendMessage(ColorUtil.colorize(getMessage("prefix") + getMessage(key)));
        }
        else if(player instanceof CommandSender){
            ((CommandSender) player).sendMessage(ColorUtil.colorize(getMessage("prefix") + getMessage(key)));
        }
    }
    public static void broadcast(String message){
        Bukkit.getServer().broadcastMessage(ColorUtil.colorize(message));
    }
    public static void broadcastPrefixed(String message){
        Bukkit.getServer().broadcastMessage(ColorUtil.colorize(prefix + message));
    }
}
