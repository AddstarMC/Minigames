package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetDisplayScoreboardCommand implements ICommand {

    @Override
    public String getName() {
        return "displayscoreboard";
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "showscoreboard",
                "dispscore",
                "displayscore",
                "showscore"
        };
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Allows or denies a Minigame from showing its scoreboard. (true by default)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame set <Minigame> displayscoreboard <true/false>"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to change scoreboard display!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.displayscoreboard";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);

            minigame.setDisplayScoreboard(bool);
            if (bool)
                sender.sendMessage(ChatColor.GRAY + "Players will now see the scoreboard in " + minigame.getName(false));
            else
                sender.sendMessage(ChatColor.GRAY + "Players will no longer see the scoreboard in " + minigame.getName(false));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args != null) {
            if (args.length == 1) {
                List<String> items = new ArrayList<>();
                items.add("true");
                items.add("false");
                return MinigameUtils.tabCompleteMatch(items, args[0]);
            }
        }
        return null;
    }

}
