package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Language.LanguageManager;

import java.text.MessageFormat;

import static pl.dcrft.Managers.Language.LanguageManager.getMessage;

public class MaintenanceManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();
    
    public static void saveAll() {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");
    }
    public static void stopServer(){
        saveAll();
        Bukkit.getServer().shutdown();
    }
    public static void restartServer(){
        saveAll();
        Bukkit.spigot().restart();
    }
    public static void reloadServer(){
        saveAll();
        Bukkit.reload();
    }
    public static void maintenanceStart(){
        saveAll();
        Bukkit.setWhitelist(true);
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!p.isOp() && !p.isWhitelisted()) {
                MessageManager.broadcastPrefixed("maintenance.maintenance.in_progress");
            }
        }
    }

    public static boolean stopServer(int minutes){
        if(minutes < 1){
            return false;
        }
        if (minutes > 1){
            saveAll();
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.stop.broadcast"), + minutes + " " + getMessage("maintenance.timeformat.minutes")));
        }
        saveAll();
        MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.stop.broadcast"), "1 " + getMessage("maintenance.timeformat.minute")));
        saveAll();
        Bukkit.getServer().setWhitelist(true);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.stop.broadcast"), "30 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.stop.broadcast"), "15 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            int i = 10;
            while (i>0){
                if(i==1){
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.stop.broadcast"), i + " " + getMessage("maintenance.timeformat.second")));
                }else {
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.stop.broadcast"), i + " " + getMessage("maintenance.timeformat.seconds")));
                }
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
            MessageManager.broadcastPrefixed("maintenance.saving");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MessageManager.broadcastPrefixed("maintenance.stop.in_progress");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveAll();
            Bukkit.getServer().setWhitelist(false);
            Bukkit.getServer().shutdown();
        }, 1000L);
        return true;
    }
    public static boolean restartServer(int minutes) {
        if (minutes < 1) {
            return false;
        }
        if (minutes > 1) {
            saveAll();
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.restart.broadcast"), +minutes + " " + getMessage("maintenance.timeformat.minutes")));
        }
        MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.restart.broadcast"), "1 " + getMessage("maintenance.timeformat.minute")));
        saveAll();
        Bukkit.getServer().setWhitelist(true);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.restart.broadcast"), "30 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.restart.broadcast"), "15 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            int i = 10;
            while (i > 0) {
                if (i == 1) {
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.restart.broadcast"), i + " " + getMessage("maintenance.timeformat.second")));
                } else {
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.restart.broadcast"), i + " " + getMessage("maintenance.timeformat.seconds")));
                }
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
            MessageManager.broadcastPrefixed("maintenance.saving");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MessageManager.broadcastPrefixed("maintenance.restart.in_progress");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            saveAll();
            Bukkit.getServer().setWhitelist(false);
            Bukkit.spigot().restart();
        }, 1000L);
        return true;
    }
    public static boolean reloadServer(int minutes){
        if(minutes < 1){
            return false;
        }
        if (minutes > 1){
            saveAll();
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.reload.broadcast"), + minutes + " " + getMessage("maintenance.timeformat.minutes")));
        }
        MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.reload.broadcast"), "1 " + LanguageManager.getMessage("maintenance.timeformat.minute")));
        saveAll();
        Bukkit.getServer().setWhitelist(true);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.reload.broadcast"), "30 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.reload.broadcast"), "15 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            int i = 10;
            while (i>0){
                if(i == 1){
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.reload.broadcast"), i + " " + getMessage("maintenance.timeformat.second")));
                }else {
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.reload.broadcast"), i + " " + getMessage("maintenance.timeformat.seconds")));
                }
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
            MessageManager.broadcastPrefixed("maintenance.saving");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MessageManager.broadcastPrefixed("maintenance.reload.in_progress");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            saveAll();
            Bukkit.getServer().setWhitelist(false);
            Bukkit.reload();
        }, 1000L);
        return true;
    }
    public static boolean maintenanceStart(int minutes){
        if(minutes < 1){
            return false;
        }
        if (minutes > 1){
            saveAll();
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.maintenance.broadcast"), + minutes + " " + getMessage("maintenance.timeformat.minutes")));
        }
        MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.maintenance.broadcast"), "1 " + LanguageManager.getMessage("maintenance.timeformat.minute")));
        saveAll();
        Bukkit.getServer().setWhitelist(true);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.maintenance.broadcast"), "30 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.maintenance.broadcast"), "15 " + getMessage("maintenance.timeformat.seconds")));
            saveAll();
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            int i = 10;
            while (i>0){
                if(i == 1){
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.maintenance.broadcast"), i + " " + getMessage("maintenance.timeformat.second")));
                }else {
                    MessageManager.broadcastPrefixed(MessageFormat.format(getMessage("maintenance.maintenance.broadcast"), i + " " + getMessage("maintenance.timeformat.seconds")));
                }
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
            MessageManager.broadcastPrefixed("maintenance.saving");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cTrwa przerwa techniczna...");
            saveAll();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveAll();
            for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!p.isOp() && !p.isWhitelisted()) {
                    MessageManager.broadcastPrefixed("maintenance.maintenance.in_progress");
                }
            }
        }, 1000L);
        return true;
    }

}
