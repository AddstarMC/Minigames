package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SelectCommand implements ICommand {
    @Override
    public @NotNull String getName() {
        return "select";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return "select and clear region selections";
    }

    @Override
    public @NotNull Component getUsage() {
        return new String[]{
                "/minigame select 1",
                "/minigame select 2",
                "/minigame select clear"
        };
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.region.select";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (sender instanceof Player player) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);

            if (args != null && args.length > 0) {
                if (args[0].equalsIgnoreCase("1")) {
                    mgPlayer.setSelection1(player.getLocation());
                    mgPlayer.sendInfoMessage(Component.text("Point 1 selected", NamedTextColor.GRAY));
                } else if (args[0].equalsIgnoreCase("2")) {
                    mgPlayer.setSelection2(player.getLocation());
                    mgPlayer.sendInfoMessage(Component.text("Point 2 selected", NamedTextColor.GRAY));

                } else if (args[0].equalsIgnoreCase("clear")) {
                    mgPlayer.clearSelection();
                    mgPlayer.sendInfoMessage(Component.text("Selection cleared.", NamedTextColor.GRAY));
                } else { // unknown param
                    return false;
                }
            } else { // not enough args
                return false;
            }
        } else {
            sender.sendMessage(Component.text("You have to be a player.", NamedTextColor.RED));
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame, String[] args) {
        if (args != null && args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("1", "2", "clear"), args[0]);
        }
        return null;
    }
}
