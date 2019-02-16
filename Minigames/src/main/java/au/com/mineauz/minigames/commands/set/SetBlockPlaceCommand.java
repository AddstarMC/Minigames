package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetBlockPlaceCommand implements ICommand {

    @Override
    public String getName() {
        return "blockplace";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"bplace"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets whether players can place blocks in Minigames. These will be reverted when the Minigame ends. (Default: false)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> blockplace <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set block placing!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.blockplace";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setCanBlockPlace(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Block placing has been enabled for " + minigame);
            } else {
                sender.sendMessage(ChatColor.GRAY + "Block placing has been disabled for " + minigame);
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
