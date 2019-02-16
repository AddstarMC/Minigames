package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetFloorDegeneratorCommand implements ICommand {
    @Override
    public String getName() {
        return "floordegenerator";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"floord", "floordegen"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Sets the two corners of a floor to degenerate or clears both of them (if set).\n" +
                "The types of degeneration are: \"inward\"(default), \"circle\" and \"random [%chance]\"(Default chance: 15).\n" +
                "Optionally, a degeneration time can be set, this defaults to the value set in the main config.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"1", "2", "clear", "type", "time"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> floordegenerator <Parameters...>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the Minigames floor area!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.floordegenerator";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            Player player = (Player) sender;
            if (args[0].equals("1")) {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() - 1);
                minigame.setFloorDegen1(loc);
                sender.sendMessage(ChatColor.GRAY + "Floor degenerator corner 1 has been set for " + minigame);
            } else if (args[0].equals("2")) {
                Location loc = player.getLocation().clone();
                loc.setY(loc.getY() - 1);
                minigame.setFloorDegen2(loc);
                sender.sendMessage(ChatColor.GRAY + "Floor degenerator corner 2 has been set for " + minigame);
            } else if (args[0].equalsIgnoreCase("clear")) {
                minigame.setFloorDegen1(null);
                minigame.setFloorDegen2(null);
                sender.sendMessage(ChatColor.GRAY + "Floor degenerator corners have been removed for " + minigame);
            } else if (args[0].equalsIgnoreCase("type") && args.length >= 2) {
                if (args[1].equalsIgnoreCase("random") || args[1].equalsIgnoreCase("inward") || args[1].equalsIgnoreCase("circle")) {
                    minigame.setDegenType(args[1].toLowerCase());
                    if (args.length > 2 && args[2].matches("[0-9]+")) {
                        minigame.setDegenRandomChance(Integer.parseInt(args[2]));
                    }
                    sender.sendMessage(ChatColor.GRAY + "Floor degenerator type has been set to " + args[1] + " in " + minigame);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid floor degenerator type!");
                    sender.sendMessage(ChatColor.GRAY + "Possible types: \"inward\", \"circle\" and \"random\".");
                }
            } else if (args[0].equalsIgnoreCase("time") && args.length >= 2) {
                if (args[1].matches("[0-9]+")) {
                    int time = Integer.parseInt(args[1]);
                    minigame.setFloorDegenTime(time);
                    sender.sendMessage(ChatColor.GRAY + "Floor degeneration time has been set to " + MinigameUtils.convertTime(time));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Error: Invalid floor degenerator command!");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("1;2;clear;type;time"), args[0]);
        return null;
    }

}
