package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetObjectiveCommand implements ICommand {

    @Override
    public String getName() {
        return "objective";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"obj"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets the objective description for the player to see when they join a Minigame. Typing \"null\" will remove the objective.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> objective <Objective Here>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the objective!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.objective";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (!args[0].equals("null")) {
                String obj = "";
                int count = 0;
                for (String arg : args) {
                    obj += arg;
                    count++;
                    if (count != args.length)
                        obj += " ";
                }
                minigame.setObjective(obj);
                sender.sendMessage(ChatColor.GRAY + "The objective for " + minigame + " has been set.");
            } else {
                minigame.setObjective(null);
                sender.sendMessage(ChatColor.GRAY + "The objective for " + minigame + " has been removed.");
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
