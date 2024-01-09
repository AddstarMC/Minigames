package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetStartTimeCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "starttime";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Overrides the default game start timer in the lobby after waiting for players time has expired or maximum players are reached. " +
                "If time is 0 then the default time is used. (Default: 0)";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> starttime <Time>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to modify the start time of a Minigame!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.starttime";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             String @NotNull [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int time = Integer.parseInt(args[0]);
                minigame.setStartWaitTime(time);
                if (time != 0) {
                    sender.sendMessage(ChatColor.GRAY + "Start time has been set to " + time + " seconds for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Start time for " + minigame + " has been reset.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a valid number!");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
