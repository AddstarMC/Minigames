package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetStartTimeCommand implements ICommand {

    @Override
    public String getName() {
        return "starttime";
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
        return "Overrides the default game start timer in the lobby after waiting for players time has expired or maximum players are reached. " +
                "If time is 0 then the default time is used. (Default: 0)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> starttime <Time>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to modify the start time of a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.starttime";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int time = Integer.parseInt(args[0]);
                minigame.setStartWaitTime(time);
                if (time != 0) {
                    sender.sendMessage(ChatColor.GRAY + "Start time has been set to " + time + " seconds for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Start time for " + minigame + " has been reset.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a valid number!");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
