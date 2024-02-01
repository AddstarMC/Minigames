package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
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
        return MinigameUtils.getLang("command.revert.description");
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame revert"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.revert";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        MinigamePlayer player = PLUGIN.getPlayerManager().getMinigamePlayer((Player) sender);

        if (player.isInMinigame()) {
            PLUGIN.getPlayerManager().revertToCheckpoint(player);
        }
        else {
            player.sendMessage(MinigameUtils.getLang("command.revert.noGlobal"), MinigameMessageType.ERROR);
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
