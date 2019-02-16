package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetMinScoreCommand implements ICommand {

    @Override
    public String getName() {
        return "minscore";
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
        return "Sets the minimum score amount for deathmatch and team deathmatch minigames. (Default: 5)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> minscore <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the minimum score for a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.minscore";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int minscore = Integer.parseInt(args[0]);
                minigame.setMinScore(minscore);
                sender.sendMessage(ChatColor.GRAY + "Minimum score has been set to " + minscore + " for " + minigame.getName(false));
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
