package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;

public class SetInfectedPercentCommand implements ICommand {

    @Override
    public String getName() {
        return "infectedpercent";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"infperc"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Sets the percentage of players that will be infected when an Infected Minigame starts. Value must be between 1 and 99.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> infectedpercent <1-99>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to set the infected start percentage!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.infectedpercent";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int val = Integer.parseInt(args[0]);
                if (val > 0 && val < 100) {
                    InfectionModule.getMinigameModule(minigame).setInfectedPercent(val);
                    sender.sendMessage(ChatColor.GRAY + "Infected percent has been set to " + val + "% for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid percentage! Value must be between 1 and 99");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a valid value! Make sure the value is between 1 and 99.");
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
