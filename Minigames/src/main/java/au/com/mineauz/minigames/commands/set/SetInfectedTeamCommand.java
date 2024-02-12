package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetInfectedTeamCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "infectedteam";
    }

    @Override
    public @NotNull String @NotNull [] getAliases() {
        return new String[]{"infteam"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_INFECTEDTEAM_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_INFECTEDTEAM_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.infectedteam";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
            TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);

            if (infectionModule != null) {
                if (teamsModule != null) {
                    TeamColor teamColor = TeamColor.matchColor(args[0]);

                    if (teamColor != null) {
                        if (teamColor == infectionModule.getDefaultInfectedTeam() ||
                                teamColor == infectionModule.getDefaultSurvivorTeam() ||
                                teamsModule.hasTeam(teamColor) ||
                                teamColor == TeamColor.NONE) {
                            infectionModule.setInfectedTeam(teamColor);
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_INFECTEDTEAM_SUCCESS,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                        }
                    } else {
                        if (args[0].equalsIgnoreCase("Default")) {
                            teamColor = infectionModule.getDefaultInfectedTeam();
                            infectionModule.setInfectedTeam(teamColor);

                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_INFECTEDTEAM_SUCCESS,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                        }
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                            Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.TEAMS.getName()));
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.INFECTION.getName()));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
        TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
        if (infectionModule != null && teamsModule != null) {
            if (args.length == 1) {
                List<String> teams = new ArrayList<>();
                for (Team t : teamsModule.getTeams()) {
                    teams.add(t.getColor().toString().toLowerCase());
                }
                teams.add(TeamColor.NONE.toString());
                teams.add("default");
                teams.add(WordUtils.capitalize(infectionModule.getDefaultInfectedTeam().toString().toLowerCase()));
                teams.add(WordUtils.capitalize(infectionModule.getDefaultSurvivorTeam().toString().toLowerCase()));
                return MinigameUtils.tabCompleteMatch(teams, args[0]);
            }
        }
        return null;
    }
}
