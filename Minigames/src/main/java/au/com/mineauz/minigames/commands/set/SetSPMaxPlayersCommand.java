package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetSPMaxPlayersCommand implements ICommand {

    @Override
    public String getName() {
        return "spmaxplayers";
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
        return "Sets whether a singleplayer game should have max players or not. (Default: false)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> spmaxplayers <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to change singleplayer max players!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.spmaxplayers";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setSpMaxPlayers(bool);
            if (bool)
                sender.sendMessage(ChatColor.GRAY + "Enabled singleplayer max players.");
            else
                sender.sendMessage(ChatColor.RED + "Disabled singleplayer max players.");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("true;false"), args[0]);
        return null;
    }

}
