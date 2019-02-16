package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetItemDropCommand implements ICommand {

    @Override
    public String getName() {
        return "itemdrop";
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
        return "Sets whether players can drop items on the ground either after they die or by dropping it themselves. (Both disabled by default)";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"player", "death"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> itemdrop player <true/false>",
                "/minigame set <Minigame> itemdrop death <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to change the drop state of items on players!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.itemdrop";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("player") && args.length >= 2) {
                boolean bool = Boolean.parseBoolean(args[1]);
                minigame.setItemDrops(bool);
                if (bool) {
                    sender.sendMessage(ChatColor.GRAY + "Item drops have been enabled for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Item drops have been disabled for " + minigame);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("death") && args.length >= 2) {
                boolean bool = Boolean.parseBoolean(args[1]);
                minigame.setDeathDrops(bool);
                if (bool) {
                    sender.sendMessage(ChatColor.GRAY + "Death drops have been enabled for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Death drops have been disabled for " + minigame);
                }
                return true;
            }
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
