package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetUsePermissionsCommand implements ICommand {

    @Override
    public String getName() {
        return "usepermissions";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"useperms"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return """
                Sets whether a player needs a specific permission to join a Minigame.\s
                Permissions as follows:\s
                "minigame.join.<minigame>" - must be all lower case""";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> usepermissions <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to change whether this minigame uses permissions (Permissionception?)";
    }

    @Override
    public String getPermission() {
        return "minigame.set.usepermissions";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setUsePermissions(bool);
            sender.sendMessage(ChatColor.GRAY + "Use permissions has been set to " + bool + " for " + minigame.getName(false));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
        return null;
    }

}
