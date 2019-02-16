package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DebugCommand implements ICommand {

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Debugs stuff.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame debug"};
    }

    @Override
    public String getPermissionMessage() {
        return "You may not debug!";
    }

    @Override
    public String getPermission() {
        return "minigame.debug";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        Minigames.getPlugin().toggleDebug();
        if (Minigames.getPlugin().isDebugging())
            sender.sendMessage(ChatColor.GRAY + "Debug mode active.");
        else
            sender.sendMessage(ChatColor.GRAY + "Deactivated debug mode.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
