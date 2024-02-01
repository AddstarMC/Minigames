package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
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

public class SpectateCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "spectate";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"spec"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Allows a player to force spectate a Minigame.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame spectate <Minigame>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.spectate";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args != null) {
            if (PLUGIN.getMinigameManager().hasMinigame(args[0])) {
                MinigamePlayer ply = PLUGIN.getPlayerManager().getMinigamePlayer((Player) sender);
                Minigame mgm = PLUGIN.getMinigameManager().getMinigame(args[0]);
                PLUGIN.getPlayerManager().spectateMinigame(ply, mgm);
            } else {
                sender.sendMessage(ChatColor.RED + "No Minigame found by the name: " + args[0]);
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
