
package pl.dcrft.Managers;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class OutputManager
{
    private static ConsoleCommandSender ccs;

    static {
        OutputManager.ccs = Bukkit.getConsoleSender();
    }

    public static void print(final Object message) {
        OutputManager.ccs.sendMessage(new StringBuilder().append(message).toString());
    }
}
