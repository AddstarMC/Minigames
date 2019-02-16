package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;

public class SetMaxHeightCommand implements ICommand {

    @Override
    public String getName() {
        return "maxheight";
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
        return "Sets the maximum Y height from the start location for a treasure chest to pick its random, however, " +
                "if there are blocks in the way, it can still move above this height. (Default: 20)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> maxheight <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to modify the treasures spawn height!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.maxheight";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int num = Integer.parseInt(args[0]);
                TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
                thm.setMaxHeight(num);
                sender.sendMessage(ChatColor.GRAY + "Maximum height variance for " + minigame + " has been set to " + num);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
