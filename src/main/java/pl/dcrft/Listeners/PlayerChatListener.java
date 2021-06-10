package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.bukkit.Bukkit.getServer;
import static pl.dcrft.Managers.ConfigManager.getDataFile;
import static pl.dcrft.Managers.Language.LanguageManager.getMessage;
import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;
import static pl.dcrft.Utils.GroupUtil.isPlayerInGroup;

public class PlayerChatListener implements Listener {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(!getDataFile().getBoolean("czat") && !e.getPlayer().hasPermission("panel.mod")){
            e.setCancelled(true);
            sendPrefixedMessage(p, "chat_muted");
            return;
        }

        List<String> red = plugin.getConfig().getStringList("redchat");
        List<String> green = plugin.getConfig().getStringList("greenchat");
        String message = e.getMessage();

        for(int i = 0; i<red.size(); i++){
            if(isPlayerInGroup(p, red.get(i))){
                e.setMessage("§c" + message);
            }
            else if(isPlayerInGroup(p, green.get(i))){
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
                String msg = MessageFormat.format(getMessage("censored_notification"), p.getName(), niezmieniona);
                getServer().getLogger().info(msg);

                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("panel.mod") && !getDataFile().getBoolean(o.getName() + ".stream")) {
                        o.sendMessage(getMessage("prefix") + msg);
                    }
                }
            }


        }
        if (e.getMessage().length() == 0) {
            e.setCancelled(true);
        }
        if (getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat")) {
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat")) {
                if (getDataFile().getBoolean(e.getPlayer().getName() + ".stream")) {
                    sendPrefixedMessage(p, "stream.cant_write_turned_off");
                    getDataFile().set(e.getPlayer().getName() + ".adminchat", false);
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
                String msg = getMessage("staffchat.adminchat.title") + getMessage("staffchat.adminchat.spacer") + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + getMessage("staffchat.adminchat.spacer") + wiad;
                getServer().getLogger().info(msg);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("admin.see") && !getDataFile().getBoolean(o.getName() + ".stream")) {
                        o.sendMessage(msg);
                    }
                }
            }
        }
        if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat")) {
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat")) {
                if (getDataFile().getBoolean(e.getPlayer().getName() + ".stream")) {
                    sendPrefixedMessage(p, "stream.cant_write_turned_off");
                    getDataFile().set(e.getPlayer().getName() + ".modchat", false);
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
                String msg = getMessage("staffchat.modchat.title") + getMessage("staffchat.modchat.spacer")  + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + getMessage("staffchat.modchat.spacer") + wiad;
                getServer().getLogger().info(msg);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("mod.see") && !getDataFile().getBoolean(o.getName() + ".stream")) {
                        o.sendMessage(msg);

                    }
                }
            }
        }
    }
}
