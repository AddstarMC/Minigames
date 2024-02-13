package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetStartCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "start";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_START_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_START_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.start";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {

        if (args != null && args[0].equalsIgnoreCase("clear")) {
            TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);

            if (teamsModule != null) {
                if (args.length >= 2) {
                    TeamColor teamColor = TeamColor.matchColor(args[1]);

                    if (teamColor != null) {
                        Team team = teamsModule.getTeam(TeamColor.matchColor(args[1]));

                        if (team != null) {
                            team.getStartLocations().clear();

                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_START_CLEAR_TEAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                            return false;
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                        return false;
                    }
                } else {
                    minigame.getStartLocations().clear();
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_START_CLEAR_SINGLE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.TEAMS.getName()));
                return false;
            }
        } else if (sender instanceof Player player) {
            int number;
            TeamColor teamColor = null;
            Location startLocation = player.getLocation();
            TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
            if (args == null) {
                number = 1;
            } else if (args.length == 1) {
                if (args[0].matches("\\d+")) {
                    number = Integer.parseInt(args[0]);
                } else {
                    if (teamsModule != null) {
                        teamColor = TeamColor.matchColor(args[0]);

                        if (teamColor != null) {
                            number = 1;
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_UNKNOWN_PARAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                            return false;
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.TEAMS.getName()));
                        return true;
                    }
                }
            } else if (teamsModule != null) {
                teamColor = TeamColor.matchColor(args[0]);

                if (teamColor != null) {
                    if (args[1].matches("\\d+")) {
                        number = Integer.parseInt(args[1]);
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                        return false;
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));

                    return false;
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.TEAMS.getName()));
                return true;
            }

            if (number > 0) {
                if (teamColor == null) {
                    minigame.addStartLocation(startLocation, number);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_START_ADD_SINGLE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(number)));
                } else {
                    Team team = teamsModule.getTeam(teamColor);

                    if (team != null) {
                        team.addStartLocation(startLocation, number);

                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_START_ADD_TEAM,
                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(number)));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), teamColor.getCompName()));
                        return false;
                    }
                }

            } else { // not valid number
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_OUTOFBOUNDS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), "0"),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(Integer.MAX_VALUE)));
                return false;
            }
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
        }

        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);

        if (teamsModule != null) {
            List<String> teams = new ArrayList<>(teamsModule.getTeamsNameMap().size() + 1);
            for (String t : teamsModule.getTeamsNameMap().keySet()) {
                teams.add(WordUtils.capitalizeFully(t.replace("_", " ")));
            }
            if (args.length == 1) {
                teams.add("Clear");
                return MinigameUtils.tabCompleteMatch(teams, args[0]);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
                return MinigameUtils.tabCompleteMatch(teams, args[1]);
            }
        } else if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("clear"), args[0]);
        }

        return null;
    }
}
