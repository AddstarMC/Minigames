package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JoinCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "join";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_JOIN_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_JOIN_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.join";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) { //todo force other players to join
        Player player = (Player) sender;
        if (args.length > 0) {
            Minigame mgm = PLUGIN.getMinigameManager().getMinigame(args[0]);
            if (mgm != null && (!mgm.getUsePermissions() || player.hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                if (!PLUGIN.getPlayerManager().getMinigamePlayer(player).isInMinigame()) {

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MinigameLangKey.PLAYER_JOIN_JOINING,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName(true)));
                    PLUGIN.getPlayerManager().joinMinigame(PLUGIN.getPlayerManager().getMinigamePlayer(player), mgm, false, 0.0);
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_JOIN_ERROR_ALREADYPLAYING);
                }
            } else if (mgm != null && mgm.getUsePermissions()) {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1 && sender instanceof Player) {
            // filter all minigames by permission
            List<String> mgs = PLUGIN.getMinigameManager().getAllMinigames().values().stream().
                    filter(mgm -> (!mgm.getUsePermissions() ||
                            sender.hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))).
                    map(mgm -> mgm.getName(false)).toList();

            return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
        }
        return null;
    }

}
