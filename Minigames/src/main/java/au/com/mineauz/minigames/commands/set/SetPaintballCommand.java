package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetPaintballCommand implements ICommand {

    @Override
    public String getName() {
        return "paintball";
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
        return "Sets a Minigame to be in paintball mode. This lets snowballs damage players. " +
                "(Default: false, default damage: 2)";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"damage"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> paintball <true/false>",
                "/minigame set <Minigame> paintball damage <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set paintball mode!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.paintball";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args.length == 1) {
                boolean bool = Boolean.parseBoolean(args[0]);
                minigame.setPaintBallMode(bool);
                if (bool) {
                    sender.sendMessage(ChatColor.GRAY + "Paintball mode has been enabled for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Paintball mode has been disabled for " + minigame);
                }
                return true;
            } else if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("damage") && args[1].matches("[0-9]+")) {
                    minigame.setPaintBallDamage(Integer.parseInt(args[1]));
                    sender.sendMessage(ChatColor.GRAY + "Paintball damage has been set to " + args[1] + " for " + minigame);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("true;false;damage"), args[0]);
        return null;
    }

}
