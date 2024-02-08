package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SPECTATE_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SPECTATE_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.spectate";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                Minigame mgm = PLUGIN.getMinigameManager().getMinigame(args[0]);
                if (mgm != null) {
                    MinigamePlayer mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(player);
                    PLUGIN.getPlayerManager().spectateMinigame(mgPlayer, mgm);
                    return true;
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
                }
            }
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
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
