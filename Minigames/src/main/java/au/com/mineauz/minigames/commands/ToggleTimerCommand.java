package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ToggleTimerCommand implements ICommand {

    @Override
    public String getName() {
        return "toggletimer";
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
        return "Toggles a multiplayer Minigames countdown timer to pause or continue.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame toggletimer <Minigame>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to toggle a Minigames timer!";
    }

    @Override
    public String getPermission() {
        return "minigame.toggletimer";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);
            if (mgm != null) {
                if (mgm.getMpTimer() != null) {
                    if (mgm.getMpTimer().isPaused()) {
                        mgm.getMpTimer().resumeTimer();
                        sender.sendMessage(ChatColor.GRAY + "Resumed " + mgm.getName(false) + "'s countdown timer.");
                    } else {
                        mgm.getMpTimer().pauseTimer(sender.getName() + " forced countdown pause.");
                        sender.sendMessage(ChatColor.GRAY + "Paused " + mgm.getName(false) + "'s countdown timer. (" + mgm.getMpTimer().getPlayerWaitTimeLeft() + "s)");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: This minigame does not have a timer running!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Error: The Minigame " + args[0] + " does not exist!");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
