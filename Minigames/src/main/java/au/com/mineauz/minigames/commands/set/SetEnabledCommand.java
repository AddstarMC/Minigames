package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetEnabledCommand implements ICommand {

    @Override
    public String getName() {
        return "enabled";
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
        return "Sets whether the Minigame is enabled or not. (Default: disabled)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> enabled <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to change the Minigames enabled state!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.enabled";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean enabled = Boolean.parseBoolean(args[0]);
            minigame.setEnabled(enabled);
            if (enabled) {
                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + " is now enabled.");
            } else {
                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + " is now disabled.");
            }
            minigame.saveMinigame();
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("true;false"), args[0]);
        return null;
    }

}
