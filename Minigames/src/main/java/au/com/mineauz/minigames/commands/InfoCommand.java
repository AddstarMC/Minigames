package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static au.com.mineauz.minigames.managers.MinigameMessageManager.MinigameLangKey;
import static au.com.mineauz.minigames.managers.MinigameMessageManager.PlaceHolderKey;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2017.
 */
public class InfoCommand implements ICommand {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public Component getDescription() {
        return MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_DESCRIPTION);
    }

    @Override
    public String[] getParameters() {
        return new String[0];
    }

    @Override
    public Component getUsage() {
        return Component.text("/minigame info [<minigame>]");
    }

    @Override
    public String getPermission() {
        new Permission("");
        return "minigame.info";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame, @NotNull String label, @NotNull String @Nullable @NotNull [] args) {
        if (args != null) {
            minigame = plugin.getMinigameManager().getMinigame(args[0]);
        }
        if (minigame != null) {
            Component output = Component.empty();

            output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_HEADER,
                    Placeholder.unparsed(PlaceHolderKey.MINIGAME.getKey(), minigame.getName(true)))).appendNewline();
            output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_DIVIDER_LARGE)).appendNewline();
            output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_DESCRIPTION,
                    Placeholder.unparsed(PlaceHolderKey.OBJECTIVE.getKey(), minigame.getObjective()))).appendNewline();
            output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_GAMETYPE,
                    Placeholder.unparsed(PlaceHolderKey.TYPE.getKey(), minigame.getType().getName()))).appendNewline();

            if (minigame.isEnabled() && minigame.hasStarted()) {
                if (minigame.getMinigameTimer() != null && minigame.getMinigameTimer().getTimeLeft() > 0) {
                    output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_TIMER,
                            Placeholder.unparsed(PlaceHolderKey.TIME.getKey(), String.valueOf(minigame.getMinigameTimer().getTimeLeft())))).appendNewline();
                }
                if (minigame.hasPlayers()) {
                    output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_PLAYERHEADER,
                            Placeholder.unparsed(PlaceHolderKey.NUMBER.getKey(), String.valueOf(minigame.getPlayers().size())),
                            Placeholder.unparsed(PlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getMaxPlayers())))).appendNewline();
                    if (minigame.isTeamGame()) {
                        for (Team team : TeamsModule.getMinigameModule(minigame).getTeams()) {
                            output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_TEAMDATA,
                                    Placeholder.unparsed(PlaceHolderKey.TEAM.getKey(), team.getDisplayName()),
                                    Placeholder.unparsed(PlaceHolderKey.SCORE.getKey(), String.valueOf(team.getScore())),
                                    Placeholder.unparsed(PlaceHolderKey.TYPE.getKey(), team.getColor().name()))).appendNewline();
                            output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_DIVIDER_SMALL)).appendNewline();
                            for (MinigamePlayer mgPlayer : team.getPlayers()) {
                                Component playerComponent = Component.text(mgPlayer.getDisplayName(minigame.usePlayerDisplayNames()));
                                if (minigame.isTeamGame()) {
                                    playerComponent = playerComponent.color(mgPlayer.getTeam().getTextColor());
                                }

                                output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_PLAYERDATA,
                                        Placeholder.component(PlaceHolderKey.PLAYER.getKey(), playerComponent),
                                        Placeholder.unparsed(PlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())),
                                        Placeholder.unparsed(PlaceHolderKey.DEATHS.getKey(), String.valueOf(mgPlayer.getDeaths())),
                                        Placeholder.unparsed(PlaceHolderKey.REVERTS.getKey(), String.valueOf(mgPlayer.getReverts())),
                                        Placeholder.unparsed(PlaceHolderKey.KILLS.getKey(), String.valueOf(mgPlayer.getKills())))).appendNewline();
                            }
                        }
                    } else {
                        for (MinigamePlayer mgPlayer : minigame.getPlayers()) {
                            Component playerComponent = Component.text(mgPlayer.getDisplayName(minigame.usePlayerDisplayNames()));
                            if (minigame.isTeamGame()) {
                                playerComponent = playerComponent.color(mgPlayer.getTeam().getTextColor());
                            }

                            output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_PLAYERDATA,
                                    Placeholder.component(PlaceHolderKey.PLAYER.getKey(), playerComponent),
                                    Placeholder.unparsed(PlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())),
                                    Placeholder.unparsed(PlaceHolderKey.DEATHS.getKey(), String.valueOf(mgPlayer.getDeaths())),
                                    Placeholder.unparsed(PlaceHolderKey.REVERTS.getKey(), String.valueOf(mgPlayer.getReverts())),
                                    Placeholder.unparsed(PlaceHolderKey.KILLS.getKey(), String.valueOf(mgPlayer.getKills()))));
                        }
                    }
                } else {
                    output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.COMMAND_INFO_OUTPUT_NOPLAYER));
                }
            } else {
                if (minigame.isEnabled()) {
                    output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.MINIGAME_ERROR_NOTSTARTED));
                } else {
                    output = output.append(MinigameMessageManager.getMessage(MinigameLangKey.MINIGAME_ERROR_NOTENABLED));
                }
            }

            MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, output);
            return true;
        } else {
            MinigameMessageManager.sendMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_INFO_OUTPUT_NOMINIGAME);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame, String alias, String[] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        }
        return null;
    }
}
