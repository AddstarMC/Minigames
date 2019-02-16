package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetAllowEnderpearlsCommand implements ICommand {

    @Override
    public String getName() {
        return "allowenderpearls";
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
        return "Sets whether players can use enderpearls in a Minigame.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> allowenderpearls <true / false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to change allow enderpearl usage!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.allowenderpearls";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            if (bool) {
                minigame.setAllowEnderpearls(bool);
                sender.sendMessage(ChatColor.GRAY + "Allowed Enderpearl usage in " + minigame);
            } else {
                minigame.setAllowEnderpearls(bool);
                sender.sendMessage(ChatColor.GRAY + "Disallowed Enderpearl usage in " + minigame);
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
