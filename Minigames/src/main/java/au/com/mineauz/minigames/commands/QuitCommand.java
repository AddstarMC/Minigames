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

public class QuitCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "quit";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"q"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_QUIT_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_QUIT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.quit";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length == 0 && sender instanceof Player player) {
            MinigamePlayer mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(player);
            if (mgPlayer.isInMinigame()) {
                PLUGIN.getPlayerManager().quitMinigame(mgPlayer, false);
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_SELF);
            }
            return true;
        } else if (args.length > 0) {
            if (sender.hasPermission("minigame.quit.other")) {
                if (args[0].equals("ALL")) {
                    if (args.length > 1) {
                        Minigame minigame = PLUGIN.getMinigameManager().getMinigame(args[1]);
                        if (minigame != null) {
                            List<MinigamePlayer> pls = new ArrayList<>(minigame.getPlayers());
                            for (MinigamePlayer pl : pls) {
                                PLUGIN.getPlayerManager().quitMinigame(pl, true);
                            }
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_QUIT_QUITALLMINIGAME,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[1]));
                        }
                    } else {
                        for (MinigamePlayer pl : PLUGIN.getPlayerManager().getAllMinigamePlayers()) {
                            if (pl.isInMinigame()) {
                                PLUGIN.getPlayerManager().quitMinigame(pl, true);
                            }
                        }
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_QUIT_QUITALL);
                    }
                    return true;
                } else {
                    MinigamePlayer mgPlayer;
                    List<Player> players = PLUGIN.getServer().matchPlayer(args[0]);

                    if (players.isEmpty()) {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                        return true;
                    } else {
                        mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(players.get(0));
                    }

                    if (mgPlayer.isInMinigame()) {
                        PLUGIN.getPlayerManager().quitMinigame(mgPlayer, false);
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_QUIT_QUITOTHER,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                    }
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>(PLUGIN.getServer().getOnlinePlayers().size() + 1);
            for (Player player : PLUGIN.getServer().getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            playerNames.add("ALL");
            return MinigameUtils.tabCompleteMatch(playerNames, args[0]);
        } else if (args.length == 2) {
            List<String> mgs = new ArrayList<>(PLUGIN.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[1]);
        }
        return null;
    }

}
