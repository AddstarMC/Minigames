package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;

public class SetRestartDelayCommand implements ICommand {

    @Override
    public String getName() {
        return "restartdelay";
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
        return "Sets how long it will take for a Treasure Hunt Minigame to respawn its treasure.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> restartdelay <time>[m|h]"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the restart delay for a treasure hunt game!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.restartdelay";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+(m|h)?")) {
                int time = Integer.parseInt(args[0].replaceAll("[mh]", ""));
                String mod = args[0].replaceAll("[0-9]", "");
                if (mod.equals("m"))
                    time *= 60;
                else if (mod.equals("h"))
                    time = time * 60 * 60;

                TreasureHuntModule.getMinigameModule(minigame).setTreasureWaitTime(time);
                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) +
                        "'s restart delay has been set to " + MinigameUtils.convertTime(time));
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
