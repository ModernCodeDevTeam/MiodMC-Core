package pl.dcrft.Utils.Error;

import pl.dcrft.DragonCraftCore;

public class ErrorUtil {
    public static DragonCraftCore plugin = DragonCraftCore.getInstance();
    public static void logError(ErrorReason type){
        if(type.equals(ErrorReason.DATABASE)) {
            plugin.getLogger().info("§e--------------------------------------------");
            plugin.getLogger().info("§cWystąpił problem podczas łączenia się z bazą danych MySQL! Wszelke przydatne informacje znajdziesz poniżej.");
            plugin.getLogger().info("§e--------------------------------------------");
        }
        else if(type.equals(ErrorReason.DATA)) {
            plugin.getLogger().info("§e--------------------------------------------");
            plugin.getLogger().info("§cWystąpił problem podczas odczytywania pliku data.yml! Wszelke przydatne informacje znajdziesz poniżej.");
            plugin.getLogger().info("§e--------------------------------------------");
        }
        else if(type.equals(ErrorReason.CONFIG)) {
            plugin.getLogger().info("§e--------------------------------------------");
            plugin.getLogger().info("§cWystąpił problem podczas odczytywania pliku config.yml! Wszelke przydatne informacje znajdziesz poniżej.");
            plugin.getLogger().info("§e--------------------------------------------");
        }
        else if(type.equals(ErrorReason.MESSAGES)) {
            plugin.getLogger().info("§e--------------------------------------------");
            plugin.getLogger().info("§cWystąpił problem podczas odczytywania pliku messages.yml! Wszelke przydatne informacje znajdziesz poniżej.");
            plugin.getLogger().info("§e--------------------------------------------");
        }
        else if(type.equals(ErrorReason.OTHER)) {
            plugin.getLogger().info("§e--------------------------------------------");
            plugin.getLogger().info("§cWystąpił nieznany problem! Wszelke przydatne informacje znajdziesz poniżej.");
            plugin.getLogger().info("§e--------------------------------------------");
        }
    }
}
