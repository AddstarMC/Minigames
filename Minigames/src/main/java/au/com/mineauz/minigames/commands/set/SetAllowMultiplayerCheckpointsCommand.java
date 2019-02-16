package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetAllowMultiplayerCheckpointsCommand implements ICommand {

    @Override
    public String getName() {
        return "multiplayercheckpoints";
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "mpcheckpoints",
                "mpcp"
        };
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Allows a Minigame to enable Checkpoint usage for multiplayer games (such as Free for All and Teams).";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> multiplayercheckpoints <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to toggle multiplayer checkpoints";
    }

    @Override
    public String getPermission() {
        return "minigame.set.multiplayercheckpoints";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setAllowMPCheckpoints(bool);
            if (bool)
                sender.sendMessage(ChatColor.GRAY + "Multiplayer checkpoints have been enabled for " + minigame);
            else
                sender.sendMessage(ChatColor.RED + "Multiplayer checkpoints have been disabled for " + minigame);

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
