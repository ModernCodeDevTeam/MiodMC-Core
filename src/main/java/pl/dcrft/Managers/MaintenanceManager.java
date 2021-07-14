package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.dcrft.DragonCraftCore;

import java.text.MessageFormat;


public class MaintenanceManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static void saveAll() {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");
            }
        });
    }

    public static void stopServer() {
        saveAll();
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Bukkit.getServer().shutdown();
            }
        });
    }

    public static void restartServer() {
        saveAll();
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Bukkit.spigot().restart();
            }
        });
    }

    public static void maintenanceStart() {
        saveAll();
        setWhitelist(true);
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!p.isOp() && !p.isWhitelisted()) {
                MessageManager.broadcastPrefixed("maintenance.maintenance.in_progress");
            }
        }
    }

    public static void setWhitelist(boolean state) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Bukkit.setWhitelist(state);
            }
        });
    }

    public static void stopServer(int minutes) {
        setWhitelist(true);
        saveAll();

        if(minutes > 1) {
            MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.stop.broadcast"), +minutes + " " + LanguageManager.getMessage("maintenance.timeformat.minutes")));
        }
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {

            saveAll();
            MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.stop.broadcast"), "1 " + LanguageManager.getMessage("maintenance.timeformat.minute")));
            saveAll();
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.stop.broadcast"), "30 " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                saveAll();
            }, 600L);
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.stop.broadcast"), "15 " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                saveAll();
            }, 900L);
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                int i = 10;
                while (i > 0) {
                    if (i == 1) {
                        MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.stop.broadcast"), i + " " + LanguageManager.getMessage("maintenance.timeformat.second")));
                    } else {
                        MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.stop.broadcast"), i + " " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                    }
                    saveAll();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                MessageManager.broadcastPrefixed(LanguageManager.getMessage("maintenance.saving"));
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MessageManager.broadcastPrefixed(LanguageManager.getMessage("maintenance.stop.in_progress"));
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                saveAll();
                setWhitelist(false);
                Bukkit.getServer().shutdown();
            }, 1000L);
        }, (minutes - 1) * 1200);
    }

    public static void restartServer(int minutes) {
        setWhitelist(true);
        saveAll();

        if(minutes > 1) {
            MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.restart.broadcast"), +minutes + " " + LanguageManager.getMessage("maintenance.timeformat.minutes")));
        }
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.restart.broadcast"), "1 " + LanguageManager.getMessage("maintenance.timeformat.minute")));
            saveAll();
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.restart.broadcast"), "30 " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                saveAll();
            }, 600L);
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.restart.broadcast"), "15 " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                saveAll();
            }, 900L);
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                int i = 10;
                while (i > 0) {
                    if (i == 1) {
                        MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.restart.broadcast"), i + " " + LanguageManager.getMessage("maintenance.timeformat.second")));
                    } else {
                        MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.restart.broadcast"), i + " " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                    }
                    saveAll();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                MessageManager.broadcastPrefixed(LanguageManager.getMessage("maintenance.saving"));
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MessageManager.broadcastPrefixed(LanguageManager.getMessage("maintenance.restart.in_progress"));
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                saveAll();
                setWhitelist(false);
                Bukkit.spigot().restart();
            }, 1000L);
        }, (minutes - 1) * 1200);
    }

    public static void maintenanceStart(int minutes) {
        setWhitelist(true);
        saveAll();

        if(minutes > 1) {
            MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.maintenance.broadcast"), +minutes + " " + LanguageManager.getMessage("maintenance.timeformat.minutes")));
        }
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {

            MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.maintenance.broadcast"), "1 " + LanguageManager.getMessage("maintenance.timeformat.minute")));
            saveAll();

            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.maintenance.broadcast"), "30 " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                saveAll();
            }, 600L);
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.maintenance.broadcast"), "15 " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                saveAll();
            }, 900L);
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                int i = 10;
                while (i > 0) {
                    if (i == 1) {
                        MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.maintenance.broadcast"), i + " " + LanguageManager.getMessage("maintenance.timeformat.second")));
                    } else {
                        MessageManager.broadcastPrefixed(MessageFormat.format(LanguageManager.getMessage("maintenance.maintenance.broadcast"), i + " " + LanguageManager.getMessage("maintenance.timeformat.seconds")));
                    }
                    saveAll();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                MessageManager.broadcastPrefixed(LanguageManager.getMessage("maintenance.saving"));
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MessageManager.broadcastPrefixed(LanguageManager.getMessage("maintenance.maintenance.in_progress"));
                saveAll();
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                saveAll();
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!p.isOp() && !p.isWhitelisted()) {
                        MessageManager.broadcastPrefixed(LanguageManager.getMessage("maintenance.maintenance.in_progress"));
                    }
                }
            }, 1000L);

        }, (minutes - 1) * 1200);
    }

}
