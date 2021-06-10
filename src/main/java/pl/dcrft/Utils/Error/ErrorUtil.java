package pl.dcrft.Utils.Error;

import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.Language.LanguageManager;

public class ErrorUtil {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();
    public static void logError(ErrorReason type){
        if(type.equals(ErrorReason.DATABASE)) {
            for (String s : LanguageManager.getMessageList("errors.database")){
                plugin.getLogger().info((s));
            }
        }
        else if(type.equals(ErrorReason.DATA)) {
            for (String s : LanguageManager.getMessageList("errors.data")){
                plugin.getLogger().info((s));
            }
        }
        else if(type.equals(ErrorReason.CONFIG)) {
            for (String s : LanguageManager.getMessageList("errors.config")){
                plugin.getLogger().info((s));
            }
        }
        else if(type.equals(ErrorReason.MESSAGES)) {
            for (String s : LanguageManager.getMessageList("errors.messages")){
                plugin.getLogger().info((s));
            }
        }
        else if(type.equals(ErrorReason.OTHER)) {
            for (String s : LanguageManager.getMessageList("errors.other")){
                plugin.getLogger().info((s));
            }
        }
    }
}
