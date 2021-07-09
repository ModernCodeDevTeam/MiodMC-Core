package pl.dcrft.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.LanguageManager;
import pl.dcrft.Managers.MessageManager;
import pl.dcrft.Utils.ColorUtil;
import pl.dcrft.Utils.GroupUtil;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class PlayerChatListener implements Listener {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(!ConfigManager.getDataFile().getBoolean("czat") && !e.getPlayer().hasPermission("panel.mod")){
            e.setCancelled(true);
            MessageManager.sendPrefixedMessage(p, "chat_muted");
            return;
        }

        List<String> red = plugin.getConfig().getStringList("redchat");
        List<String> green = plugin.getConfig().getStringList("greenchat");
        String message = e.getMessage();

        for(int i = 0; i<red.size(); i++){
            if(GroupUtil.isPlayerInGroup(p, red.get(i))){
                e.setMessage("§c" + message);
            }
            else if(GroupUtil.isPlayerInGroup(p, green.get(i))){
                e.setMessage("§a" + message);
            }

        }
        String niezmieniona = message;
        for (final Map.Entry<String, Object> filter : plugin.filters.entrySet()) {
            message = message.toLowerCase().replaceAll(filter.getKey().toLowerCase(), filter.getValue().toString());
        }
        if (!message.equalsIgnoreCase(niezmieniona)) {
            if(e.getPlayer().isOp()){
                return;
            }
            e.setMessage(message);
            String[] words = plugin.getConfig().getStringList("replacement").toArray(new String[0]);

            String finalMessage = message;
            if (Stream.of(words).noneMatch(word -> finalMessage.contains(word.toLowerCase()))) {
                String msg = MessageFormat.format(LanguageManager.getMessage("censored_notification"), p.getName(), niezmieniona);
                Bukkit.getServer().getLogger().info(msg);

                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("panel.mod") && !ConfigManager.getDataFile().getBoolean("players." + o.getName() + ".stream")) {
                        o.sendMessage(LanguageManager.getMessage("prefix") + msg);
                    }
                }
            }


        }
        if (e.getMessage().length() == 0) {
            e.setCancelled(true);
        }
        if (ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".adminchat")) {
            if (ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".adminchat")) {
                if (ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".stream")) {
                    MessageManager.sendPrefixedMessage(p, "stream.cant_write_turned_off");
                    ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".adminchat", false);
                    e.setCancelled(true);
                    ConfigManager.saveData();
                    return;
                }
                e.setCancelled(true);
                Player sender = e.getPlayer();
                String wiad;
                if (!message.equalsIgnoreCase(niezmieniona)) {
                    wiad = message;
                }
                else {
                    wiad = niezmieniona;
                }
                String msg = ColorUtil.colorize(
                        LanguageManager.getMessage("staffchat.adminchat.title") +
                                LanguageManager.getMessage("staffchat.adminchat.spacer") +
                                sender.getDisplayName() +
                                LanguageManager.getMessage("staffchat.adminchat.spacer") +
                                wiad);
                Bukkit.getServer().getLogger().info(msg);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("admin.see") && !ConfigManager.getDataFile().getBoolean("players." + o.getName() + ".stream")) {
                        o.sendMessage(msg);
                    }
                }
            }
        }
        if (ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".modchat")) {
            if (ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".modchat")) {
                if (ConfigManager.getDataFile().getBoolean("players." + e.getPlayer().getName() + ".stream")) {
                    MessageManager.sendPrefixedMessage(p, "stream.cant_write_turned_off");
                    ConfigManager.getDataFile().set("players." + e.getPlayer().getName() + ".modchat", false);
                    e.setCancelled(true);
                    ConfigManager.saveData();
                    return;
                }
                e.setCancelled(true);
                Player sender = e.getPlayer();
                String wiad;
                if (!message.equalsIgnoreCase(niezmieniona)) {
                    wiad = message;
                }
                else {
                    wiad = niezmieniona;
                }
                String msg = ColorUtil.colorize(
                        LanguageManager.getMessage("staffchat.modchat.title") +
                                LanguageManager.getMessage("staffchat.modchat.spacer")  +
                                sender.getDisplayName() +
                                LanguageManager.getMessage("staffchat.modchat.spacer") +
                                wiad);
                Bukkit.getServer().getLogger().info(msg);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("mod.see") && !ConfigManager.getDataFile().getBoolean("players." + o.getName() + ".stream")) {
                        o.sendMessage(msg);

                    }
                }
            }
        }
    }
}
