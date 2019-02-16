package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetItemPickupCommand implements ICommand {

    @Override
    public String getName() {
        return "itempickup";
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
        return "Changes whether a player can pickup items when in a Minigame. (Enabled by default)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> itempickup <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to change the item pickup state in a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.itempickup";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setItemPickup(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Item pickup has been enabled for " + minigame);
            } else {
                sender.sendMessage(ChatColor.GRAY + "Item pickup has been disabled for " + minigame);
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
