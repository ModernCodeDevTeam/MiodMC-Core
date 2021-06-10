package pl.dcrft.Managers;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Language.LanguageManager;

import java.util.ArrayList;

public class SessionManager {

    public static final ArrayList<SessionManager> list = new ArrayList<>();
    private final Player p;

    private Location lastLoc;

    private int afkMinutes;
    private static final DragonCraftCore plugin = DragonCraftCore.getInstance();
    private static final String prefix = LanguageManager.getMessage("prefix");

    public SessionManager(Player p) {
        this.p = p;
        this.lastLoc = p.getLocation();
        this.afkMinutes = 0;
    }

    public void resetMinute() {
        if (this.p != null && this.p.isOnline())
            if (!this.p.hasPermission("afkkick.ignore")) {
                this.afkMinutes = 0;
                this.lastLoc = this.p.getLocation();
            }
    }

    public void increaseMinute() {
        int kick_warn_delay = plugin.getConfig().getInt("afk.kick_warn_delay");
        int kick_delay = plugin.getConfig().getInt("afk.kick_delay");
        if (this.p != null && this.p.isOnline())
            if (!this.p.hasPermission("afkkick.ignore"))
                if (this.lastLoc.getWorld() != this.p.getLocation().getWorld()) {
                    return;
                }
        if (this.lastLoc.distanceSquared(this.p.getLocation()) < 4.0D) {
            this.afkMinutes++;
            this.lastLoc = this.p.getLocation();
            if (this.afkMinutes == kick_warn_delay) {
                boolean sound_on_get_warn = Boolean.parseBoolean(plugin.getConfig().getString("afk.sound_on_get_warn"));

                if (sound_on_get_warn) {
                    this.p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10,3);
                }
                MessageManager.sendPrefixedMessage(p, "afk.kick_warn_msg");
                TextComponent tc = new TextComponent();
                tc.setText(LanguageManager.getMessage("afk.kick_warn_msg_afk"));
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nieafk"));
                p.spigot().sendMessage(tc);
            } else if(this.afkMinutes >= kick_delay) {
                kickPlayer();
            }
        } else {
            this.afkMinutes = 0;
            this.lastLoc = this.p.getLocation();
        }
    }

    private void kickPlayer() {
        if (!this.p.hasPermission("afkkick.ignore"))
            Bukkit.getScheduler().scheduleSyncDelayedTask(DragonCraftCore.getInstance(), () -> SessionManager.this.p.kickPlayer(prefix + LanguageManager.getMessage("afk.kick_msg")),  20L);
    }


    public Player getPlayer() {
        return this.p;
    }
}