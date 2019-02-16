package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetFlightCommand implements ICommand {

    @Override
    public String getName() {
        return "flight";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"fly"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets whether a player is allowed to fly in a Minigame and whether they are flying when they join or start the game.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"enabled", "startflying"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> flight <Parameter> <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to modify flight in a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.flight";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null && args.length == 2) {
            if (args[0].equalsIgnoreCase("enabled")) {
                boolean bool = Boolean.parseBoolean(args[1]);
                minigame.setAllowedFlight(bool);

                if (bool)
                    sender.sendMessage(ChatColor.GRAY + "Players are now allowed to fly in " + minigame);
                else
                    sender.sendMessage(ChatColor.GRAY + "Players are now not able to fly in " + minigame);
                return true;
            } else if (args[0].equalsIgnoreCase("startflying")) {
                boolean bool = Boolean.parseBoolean(args[1]);
                minigame.setFlightEnabled(bool);

                if (bool)
                    sender.sendMessage(ChatColor.GRAY + "Players will start flying when the game starts in " + minigame);
                else
                    sender.sendMessage(ChatColor.GRAY + "Players will not start flying when the game starts in " + minigame);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args != null && args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("enabled;startflying"), args[0]);
        else if (args != null && args.length == 2)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("true;false"), args[1]);
        return null;
    }

}
