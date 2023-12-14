package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetLateJoinCommand implements ICommand {

    @Override
    public String getName() {
        return "latejoin";
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
        return "Enables a player to join after the game has already started. This can only be used in Multiplayer Minigames.\n" +
                "Warning: Do not use this in an LMS Minigame, for obvious reasons.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> latejoin <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to enable late joining to a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.latejoin";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setLateJoin(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Late join has been enabled for " + minigame);
            } else {
                sender.sendMessage(ChatColor.GRAY + "Late join has been disabled for " + minigame);
            }
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
