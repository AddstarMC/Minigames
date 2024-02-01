package au.com.mineauz.minigames.commands;

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

public class RevertCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "revert";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"r"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_REVERT_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_REVERT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.revert";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            MinigamePlayer mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(player);

            if (mgPlayer.isInMinigame() && mgPlayer.getCheckpoint() != null) {
                PLUGIN.getPlayerManager().revertToCheckpoint(mgPlayer);
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_REVERT_ERROR_NOCHECKPOINTS);
            }
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
