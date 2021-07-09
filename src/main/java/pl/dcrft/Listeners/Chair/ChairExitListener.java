package pl.dcrft.Listeners.Chair;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;
import pl.dcrft.Managers.LanguageManager;

public class ChairExitListener implements Listener {
    final String prefix = LanguageManager.getMessage("prefix");
    @EventHandler
    public void onChairExit(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (event.getDismounted() instanceof ArmorStand) {
                ArmorStand armorStand = (ArmorStand) event.getDismounted();
                if(armorStand.getCustomName().equals("Chair")){
                    armorStand.remove();
                    p.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(prefix + LanguageManager.getMessage("chairs.exit")));
                }
            }
        }
    }
}
