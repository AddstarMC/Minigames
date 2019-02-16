package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetBlockBreakCommand implements ICommand {

    @Override
    public String getName() {
        return "blockbreak";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"bbreak"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets whether players can break blocks in Minigames. These will be reverted when the Minigame ends. (Default: false)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> blockbreak <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set block breaking!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.blockbreak";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setCanBlockBreak(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Block breaking has been enabled for " + minigame);
            } else {
                sender.sendMessage(ChatColor.GRAY + "Block breaking has been disabled for " + minigame);
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
