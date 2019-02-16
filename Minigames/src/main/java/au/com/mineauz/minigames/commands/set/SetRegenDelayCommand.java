package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetRegenDelayCommand implements ICommand {

    @Override
    public String getName() {
        return "regendelay";
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
        return "Sets the amount of time in seconds the Minigame Regenerator should wait before starting its regen. " +
                "Useful for TNT explosions that could go off even after the games over. (Default: 0 seconds)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> regendelay <TimeInSeconds>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to modify the regeneration delay!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.regendelay";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null && args[0].matches("[0-9]+")) {
            int time = Integer.parseInt(args[0]);
            minigame.setRegenDelay(time);
            sender.sendMessage(ChatColor.GRAY + "Set " + minigame.getName(false) + "'s regeneration delay to " + time + " seconds.");
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
