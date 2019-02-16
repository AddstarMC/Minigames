package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetSpectateCommand implements ICommand {

    @Override
    public String getName() {
        return "spectatefly";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"specfly"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Enables or disabled spectator fly mode for a Minigame. (Default: false)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> spectatefly <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to enable or disable spectator fly mode in a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.spectatefly";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setCanSpectateFly(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Enabled spectator flying in " + minigame);
            } else
                sender.sendMessage(ChatColor.GRAY + "Disabled spectator flying in " + minigame);
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
