package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SelectCommand extends ACommand {
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
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SELECT_DESCRIPTION);
    }

    @Override
    public @NotNull Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SELECT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.region.select";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("1")) {
                    mgPlayer.setSelection1(player.getLocation());
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SELECT_POINT1);
                } else if (args[0].equalsIgnoreCase("2")) {
                    mgPlayer.setSelection2(player.getLocation());
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SELECT_POINT2);

                } else if (args[0].equalsIgnoreCase("clear")) {
                    mgPlayer.clearSelection();
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SELECT_CLEAR);
                } else { // unknown param
                    return false;
                }
            } else { // not enough args
                return false;
            }
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("1", "2", "clear"), args[0]);
        }
        return null;
    }
}
