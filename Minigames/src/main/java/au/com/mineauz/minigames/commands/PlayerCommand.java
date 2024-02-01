package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "player";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"ply", "pl"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.player";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("list")) {
                List<MinigamePlayer> mgPlayers = new ArrayList<>();
                for (MinigamePlayer pl : Minigames.getPlugin().getPlayerManager().getAllMinigamePlayers()) {
                    if (pl.isInMinigame()) {
                        mgPlayers.add(pl);
                    }
                }

                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_PLAYER_LIST_HEADER);
                if (!mgPlayers.isEmpty()) {
                    for (MinigamePlayer mgPlayer : mgPlayers) {
                        //todo don't send x trillion messages. merge to list and send as pages
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_PLAYER_LIST_ENTRY,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgPlayer.getMinigame().getName(false)), //  mgPlayer.getMinigame() is never null, we only add player in Minigame to list
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MinigameLangKey.MINIGAME_INFO_PLAYERS_NONE);
                }
            } else {
                List<Player> playerMatch = Bukkit.matchPlayer(args[0]);
                if (!playerMatch.isEmpty()) {
                    MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(playerMatch.get(0));
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_PLAYER_PLAYERINFO_HEADER,
                            Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                    if (mgPlayer.isInMinigame()) {
                        Duration playTime = Duration.ofMillis(Calendar.getInstance().getTimeInMillis() - mgPlayer.getStartTime() + mgPlayer.getStoredTime());

                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_PLAYERINFO_MINIGAME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgPlayer.getMinigame().getName(false)));
                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_PLAYERINFO_SCORE,
                                Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())));
                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_PLAYERINFO_KILLS,
                                Placeholder.unparsed(MinigamePlaceHolderKey.KILLS.getKey(), String.valueOf(mgPlayer.getKills())));
                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_PLAYERINFO_DEATHS,
                                Placeholder.unparsed(MinigamePlaceHolderKey.DEATHS.getKey(), String.valueOf(mgPlayer.getDeaths())));
                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_PLAYERINFO_REVERTS,
                                Placeholder.unparsed(MinigamePlaceHolderKey.REVERTS.getKey(), String.valueOf(mgPlayer.getReverts())));
                        MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_PLAYER_PLAYERINFO_PLAYTIME,
                                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(playTime)));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_PLAYER,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> plys = new ArrayList<>(PLUGIN.getPlayerManager().getAllMinigamePlayers()).stream().
                    map(MinigamePlayer::getName).collect(Collectors.toCollection(ArrayList::new));

            plys.add("list");
            return MinigameUtils.tabCompleteMatch(plys, args[0]);
        }
        return null;
    }

}
