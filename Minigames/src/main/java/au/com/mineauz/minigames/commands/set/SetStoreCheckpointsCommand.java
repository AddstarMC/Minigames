package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetStoreCheckpointsCommand implements ICommand {

    @Override
    public String getName() {
        return "storecheckpoints";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"storecp", "spc"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "When enabled, if a player quits from a single player Minigame, their checkpoint will be stored so they can join at" +
                "that position later.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> storecheckpoints <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to enable or disable storing of checkpoints!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.storecheckpoints";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            Boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setSaveCheckpoint(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Checkpoint saving has been enabled for " + minigame);
            } else {
                sender.sendMessage(ChatColor.GRAY + "Checkpoint saving has been disabled for " + minigame);
            }
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
