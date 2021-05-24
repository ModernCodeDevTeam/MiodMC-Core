package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Language.LanguageManager;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.bukkit.Bukkit.getServer;
import static pl.dcrft.Managers.ConfigManger.getDataFile;
import static pl.dcrft.Managers.DataManager.saveData;
import static pl.dcrft.Managers.Language.LanguageManager.getMessage;
import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;
import static pl.dcrft.Utils.GroupUtil.isPlayerInGroup;

public class PlayerChatListener implements Listener {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(!getDataFile().getBoolean("czat") && !e.getPlayer().hasPermission("panel.mod")){
            e.setCancelled(true);
            sendPrefixedMessage(p, "chat_muted");
            return;
        }

        List<String> red = plugin.getConfig().getStringList("czerwonyczat");
        List<String> green = plugin.getConfig().getStringList("zielonyczat");
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
        for (final Map.Entry<String, Object> filter : plugin.filtry.entrySet()) {
            message = message.toLowerCase().replaceAll(filter.getKey().toLowerCase(), filter.getValue().toString());
        }
        if (!message.equalsIgnoreCase(niezmieniona)) {
            e.setMessage(message);
            String[] words = plugin.getConfig().getStringList("zastepowanie").toArray(new String[0]);

            String finalMessage = message;
            if (!Stream.of(words).anyMatch(word -> finalMessage.contains(word.toLowerCase()))) {
                getServer().getLogger().info("§e" + e.getPlayer().getName() + " §cużył niedozwolonego słowa §e» §e" + niezmieniona);

                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("panel.mod") && getDataFile().getBoolean(o.getName() + ".stream") == false) {
                        String msg = MessageFormat.format(getMessage("censored_notification"), p.getName(), niezmieniona);
                        o.sendMessage(getMessage("prefix") + msg);
                    }
                }
            }


        }
        if (e.getMessage().length() == 0) {
            e.setCancelled(true);
        }
        if (getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat")) {
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".adminchat") == true) {
                if (getDataFile().getBoolean(e.getPlayer().getName() + ".stream") == true) {
                    sendPrefixedMessage(p, "stream.cant_write_turned_off");
                    getDataFile().set(e.getPlayer().getName() + ".adminchat", false);
                    e.setCancelled(true);
                    saveData();
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
                getServer().getLogger().info("§c§lAdmin§4§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §c" + wiad);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("admin.see") && getDataFile().getBoolean(o.getName() + ".stream") == false) {
                        o.sendMessage("§c§lAdmin§4§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §c" + wiad);
                    }
                }
            }
        }
        if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat")) {
            if (getDataFile().getBoolean(e.getPlayer().getName() + ".modchat") == true) {
                if (getDataFile().getBoolean(e.getPlayer().getName() + ".stream") == true) {
                    sendPrefixedMessage(p, "stream.cant_write_turned_off");
                    getDataFile().set(e.getPlayer().getName() + ".modchat", false);
                    e.setCancelled(true);
                    saveData();
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
                getServer().getLogger().info("§a§lMod§2§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §c" + wiad);
                for(Player o : Bukkit.getOnlinePlayers()){
                    if(o.hasPermission("mod.see") && getDataFile().getBoolean(o.getName() + ".stream") == false) {
                        o.sendMessage("§a§lMod§2§lChat §e» " + PlaceholderAPI.setPlaceholders(sender, "%vault_prefix%") + sender.getName() + "§e » §a" + wiad);

                    }
                }
            }
        }
    }
}
