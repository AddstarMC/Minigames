package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2017.
 */
public class InfoCommand extends ACommand {
    @Override
    public @NotNull String getName() {
        return "info";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return Component.text("/minigame info [<minigame>]");
    }

    @Override
    public @Nullable String getPermission() {
        new Permission("");
        return "minigame.info";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        Minigame minigame;
        if (args.length > 0) {
            minigame = PLUGIN.getMinigameManager().getMinigame(args[0]);
        } else {
            if (sender instanceof Player player) {
                minigame = PLUGIN.getPlayerManager().getMinigamePlayer(player).getMinigame();
            } else {
                minigame = null;
            }
        }

        if (minigame != null) {
            TextComponent.Builder outputBuilder = Component.text();

            outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_HEADER,
                    Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getDisplayName()))).appendNewline();
            outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DIVIDER_LARGE)).appendNewline();
            outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_DESCRIPTION,
                    Placeholder.component(MinigamePlaceHolderKey.OBJECTIVE.getKey(), minigame.getObjective()))).appendNewline();
            outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_GAMETYPE,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), minigame.getType().getName()))).appendNewline();

            if (minigame.isEnabled() && minigame.hasStarted()) {
                if (minigame.getMinigameTimer() != null && minigame.getMinigameTimer().getTimeLeft() > 0) {
                    outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_TIMER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(minigame.getMinigameTimer().getTimeLeft())))).appendNewline();
                }
                if (minigame.hasPlayers()) {
                    outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_PLAYERHEADER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(minigame.getPlayers().size())),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getMaxPlayers())))).appendNewline();
                    if (minigame.isTeamGame()) {
                        for (Team team : TeamsModule.getMinigameModule(minigame).getTeams()) {
                            outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_TEAMDATA,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), team.getDisplayName()),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(team.getScore())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), team.getColor().name()))).appendNewline();
                            outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_DIVIDER_SMALL)).appendNewline();
                            for (MinigamePlayer mgPlayer : team.getPlayers()) {
                                Component playerComponent = Component.text(mgPlayer.getDisplayName(minigame.usePlayerDisplayNames()));
                                if (minigame.isTeamGame()) {
                                    playerComponent = playerComponent.color(mgPlayer.getTeam().getTextColor());
                                }

                                outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_PLAYERDATA,
                                        Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), playerComponent),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.DEATHS.getKey(), String.valueOf(mgPlayer.getDeaths())),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.REVERTS.getKey(), String.valueOf(mgPlayer.getReverts())),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.KILLS.getKey(), String.valueOf(mgPlayer.getKills())))).appendNewline();
                            }
                        }
                    } else {
                        for (MinigamePlayer mgPlayer : minigame.getPlayers()) {
                            Component playerComponent = Component.text(mgPlayer.getDisplayName(minigame.usePlayerDisplayNames()));
                            if (minigame.isTeamGame()) {
                                playerComponent = playerComponent.color(mgPlayer.getTeam().getTextColor());
                            }

                            outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_PLAYERDATA,
                                    Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), playerComponent),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.DEATHS.getKey(), String.valueOf(mgPlayer.getDeaths())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.REVERTS.getKey(), String.valueOf(mgPlayer.getReverts())),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.KILLS.getKey(), String.valueOf(mgPlayer.getKills()))));
                        }
                    }
                } else {
                    outputBuilder.append(MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_INFO_OUTPUT_NOPLAYER));
                }
            } else {
                if (minigame.isEnabled()) {
                    outputBuilder.append(MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_ERROR_NOTSTARTED));
                } else {
                    outputBuilder.append(MinigameMessageManager.getMgMessage(MinigameLangKey.MINIGAME_ERROR_NOTENABLED));
                }
            }

            MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, outputBuilder.build());
            return true;
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_INFO_OUTPUT_NOMINIGAME);
        }

        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>(PLUGIN.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        }
        return null;
    }
}
