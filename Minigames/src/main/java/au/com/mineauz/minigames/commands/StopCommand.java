package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StopCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "stop";
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
        return "Stops a currently running Global Minigame.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame stop <Minigame>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to stop Global Minigames!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.stop";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);

            if (mgm != null && mgm.isEnabled() && mgm.getType() == MinigameType.GLOBAL) {
                MinigamePlayer caller = null;
                if (sender instanceof Player)
                    caller = plugin.getPlayerManager().getMinigamePlayer((Player) sender);
                plugin.getMinigameManager().stopGlobalMinigame(mgm, caller);
            } else if (mgm == null || mgm.getType() != MinigameType.GLOBAL) {
                sender.sendMessage(ChatColor.RED + "There is no Global Minigame by the name \"" + args[0] + "\"");
            } else {
                sender.sendMessage(ChatColor.RED + mgm.getName(false) + " is not running!");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        List<String> mgs = new ArrayList<>();
        for (Minigame mg : plugin.getMinigameManager().getAllMinigames().values()) {
            if (mg.getType() == MinigameType.GLOBAL)
                mgs.add(mg.getName(false));
        }
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }
}
