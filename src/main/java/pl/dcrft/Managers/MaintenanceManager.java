package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.dcrft.DragonCraftCore;

import static pl.dcrft.Managers.Language.LanguageManager.getMessage;

public class MaintenanceManager {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();;
    public static void stopServer(){
        Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 1 minutę serwer zostanie zatrzymany.");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist on");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 30 sekund serwer zostanie zatrzymany.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 15 sekund serwer zostanie zatrzymany.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                int i = 10;
                while (i>0){
                    Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa " + i + " sekund serwer zostanie zatrzymany.");
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZapisywanie plik\u00f3w \u015bwiata..");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cTrwa zatrzymywanie serwera...");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist off");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "stop");
            }
        }, 1000L);
    }
    public static void restartServer(){
        Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 1 minutę serwer zostanie uruchomiony ponownie.");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist on");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 30 sekund serwer zostanie uruchomiony ponownie.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 15 sekund serwer zostanie uruchomiony ponownie.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                int i = 10;
                while (i>0){
                    Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa " + i + " sekund serwer zostanie uruchomiony ponownie.");
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZapisywanie plik\u00f3w \u015bwiata..");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cTrwa ponowne uruchamiani serwera...");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist off");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
            }
        }, 1000L);
    }
    public static void reloadServer(){
        Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 1 minutę pliki serwera zostaną przeładowane.");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist on");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 30 sekund pliki serwera zostaną przeładowane.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 15 sekund pliki serwera zostaną przeładowane.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                int i = 10;
                while (i>0){
                    Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa " + i + " sekund pliki serwera zostaną przeładowane.");
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZapisywanie plik\u00f3w \u015bwiata..");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cTrwa ponowne ładowanie plików serwera...");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist off");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "reload confirm");
            }
        }, 1000L);
    }
    public static void maintenanceStart(){
        Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 1 minutę odbędzie się przerwa techniczna.");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist on");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 30 sekund odbędzie się przerwa techniczna.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 600L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa 15 sekund pliki sodbędzie się przerwa techniczna.");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
            }
        }, 900L);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                int i = 10;
                while (i>0){
                    Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZa " + i + " sekund odbędzie się przerwa techniczna.");
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cZapisywanie plik\u00f3w \u015bwiata..");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bukkit.getServer().broadcastMessage(getMessage("prefix") + " §cTrwa przerwa techniczna...");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!p.isOp() && !p.isWhitelisted()) {
                        p.kickPlayer(getMessage("prefix") + " §cTrwa przerwa techniczna...");
                    }
                }
            }
        }, 1000L);
    }

}
