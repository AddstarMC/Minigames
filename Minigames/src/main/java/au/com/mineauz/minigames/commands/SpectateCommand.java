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

public class SpectateCommand implements ICommand {

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
    public @NotNull String @Nullable [] getParameters() {
        return null;
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
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (plugin.getMinigameManager().hasMinigame(args[0])) {
                MinigamePlayer ply = plugin.getPlayerManager().getMinigamePlayer((Player) sender);
                Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);
                plugin.getPlayerManager().spectateMinigame(ply, mgm);
            } else {
                sender.sendMessage(ChatColor.RED + "No Minigame found by the name: " + args[0]);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
