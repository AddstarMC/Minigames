package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;

public class SetMaxTreasureCommand implements ICommand {

    @Override
    public String getName() {
        return "maxtreasure";
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
        return "Sets the maximum number of items to spawn in a treasure hunt chest.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> maxtreasure <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the max treasure for a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.maxtreasure";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int amount = Integer.parseInt(args[0]);
                TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
                thm.setMaxTreasure(amount);
                sender.sendMessage(ChatColor.GRAY + "Maximum items has been set to " + amount + " for " + minigame);
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
