package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetTimerCommand implements ICommand {

    @Override
    public String getName() {
        return "timer";
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
        return "Sets the maximum time length (in seconds) for a Minigame. Adding 'm' or 'h' to the end of the time will use " +
                "minutes or hours instead. The highest score at the end of this time wins.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> timer <Number>[m|h]", "/minigame set <Minigame> timer useXPBar <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the Minigames time length!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.timer";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+[mh]?")) {
                boolean hours = false;
                boolean minutes = false;
                if (args[0].contains("h")) {
                    hours = true;
                } else if (args[0].contains("m")) {
                    minutes = true;
                }

                int time = Integer.parseInt(args[0].replace("m", "").replace("h", ""));
                if (hours) {
                    time = time * 60 * 60;
                } else if (minutes) {
                    time = time * 60;
                }
                minigame.setTimer(time);
                if (time != 0) {
                    sender.sendMessage(ChatColor.GRAY + "The timer for \"" + minigame + "\" has been set to " + MinigameUtils.convertTime(time) + ".");
                } else {
                    sender.sendMessage(ChatColor.GRAY + "The timer for \"" + minigame + "\" has been removed.");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("usexpbar") && args.length == 2) {
                boolean bool = Boolean.parseBoolean(args[1]);
                minigame.setUseXPBarTimer(bool);
                if (bool)
                    sender.sendMessage(ChatColor.GRAY + minigame.toString() + " will now show the timer in the XP bar.");
                else
                    sender.sendMessage(ChatColor.GRAY + minigame.toString() + " will no longer show the timer in the XP bar.");
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
