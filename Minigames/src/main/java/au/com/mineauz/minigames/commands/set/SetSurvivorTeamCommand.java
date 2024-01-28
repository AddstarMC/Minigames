package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
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

public class SetSurvivorTeamCommand implements ICommand {
    @Override
    public @NotNull String getName() {
        return "survivorteam";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"svteam"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_SURVIVORTEAM_DESCRIPTION);
    }
    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_SURVIVORTEAM_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.survivorteam";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);

        if (args != null) {
            if (infectionModule != null) {
                TeamColor teamColor = TeamColor.matchColor(args[0]);

                if (args[0].equalsIgnoreCase("Default")) {
                    teamColor = infectionModule.getDefaultSurvivorTeam();
                }

                if (teamColor != null) {
                    TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);

                    if (teamColor == infectionModule.getDefaultInfectedTeam() ||
                            teamColor == infectionModule.getDefaultSurvivorTeam() ||
                            (teamsModule != null && teamsModule.hasTeam(teamColor))) {
                        infectionModule.setSurvivorTeam(teamColor);
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_SURVIVORTEAM_SUCCESS,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                    return false;
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.INFECTION.getName()));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
            if (infectionModule != null) {
                List<String> teams = new ArrayList<>();

                TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
                if (teamsModule != null) {
                    for (Team t : teamsModule.getTeams()) {
                        teams.add(t.getColor().toString().toLowerCase());
                    }
                }

                teams.add(TeamColor.NONE.toString());
                teams.add("default");

                teams.add(WordUtils.capitalizeFully(infectionModule.getDefaultInfectedTeam().toString().toLowerCase().replace("_", " ")));
                teams.add(WordUtils.capitalizeFully(infectionModule.getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));

                return MinigameUtils.tabCompleteMatch(teams, args[0]);
            }
        }
        return null;
    }
}
