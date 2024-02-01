package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToggleTimerCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "toggletimer";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Toggles a multiplayer Minigames countdown timer to pause or continue.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame toggletimer <Minigame>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.toggletimer";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args != null) {
            Minigame mgm = PLUGIN.getMinigameManager().getMinigame(args[0]);
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
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        List<String> mgs = new ArrayList<>(PLUGIN.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
