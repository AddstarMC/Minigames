package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetMaxPlayersCommand implements ICommand {

    @Override
    public String getName() {
        return "maxplayers";
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
        return "Sets the maximum players allowed to play a multiplayer Minigame. (Default: 4)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> maxplayers <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the maximum players for a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.maxplayers";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int max = Integer.parseInt(args[0]);
                minigame.setMaxPlayers(max);
                sender.sendMessage(ChatColor.GRAY + "Maximum players has been set to " + max + " for " + minigame);
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
