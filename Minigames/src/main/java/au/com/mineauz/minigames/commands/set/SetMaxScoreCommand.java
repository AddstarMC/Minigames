package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetMaxScoreCommand implements ICommand {

    @Override
    public String getName() {
        return "maxscore";
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
        return "Sets the maximum score for a deathmatch or team deathmatch Minigame. (Default: 10)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> maxscore <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the maximum score of a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.maxscore";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int maxscore = Integer.parseInt(args[0]);
                minigame.setMaxScore(maxscore);
                sender.sendMessage(ChatColor.GRAY + "Maximum score has been set to " + maxscore + " for " + minigame.getName(false));
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
