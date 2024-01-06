package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetInfectedTeamCommand implements ICommand {
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
        return "Set which team color will represent the Infected team in an Infection Minigame (Default: red)";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> infectedteam <TeamColor>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.infectedteam";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame, @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);

            if (args[0].equalsIgnoreCase("None")) {
                infectionModule.setInfectedTeam(null);
                sender.sendMessage(ChatColor.GRAY + "The infected team of " + minigame + " has been set to none.");
            } else if (args[0].equalsIgnoreCase("Default")) {
                infectionModule.setInfectedTeam(WordUtils.capitalize(infectionModule.getDefaultInfectedTeam().toString().toLowerCase().replace("_", " ")));
                sender.sendMessage(ChatColor.GRAY + "The infected team of " + minigame + " has been set to " +
                        WordUtils.capitalize(infectionModule.getDefaultInfectedTeam().toString().toLowerCase().replace("_", " ")));
            } else {
                if (TeamColor.matchColor(args[0]) == infectionModule.getDefaultInfectedTeam() ||
                        TeamColor.matchColor(args[0]) == infectionModule.getDefaultSurvivorTeam() ||
                        TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.matchColor(args[0]))) {
                    infectionModule.setInfectedTeam(args[0]);
                    sender.sendMessage(ChatColor.GRAY + "The infected team of " + minigame + " has been set to " + args[0] + ".");
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTTEAM,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame, String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> teams = new ArrayList<>();
            for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                teams.add(t.getColor().toString().toLowerCase());
            }
            teams.add("none");
            teams.add("default");
            InfectionModule infectionModule = InfectionModule.getMinigameModule(minigame);
            teams.add(WordUtils.capitalize(infectionModule.getDefaultInfectedTeam().toString().toLowerCase().replace("_", " ")));
            teams.add(WordUtils.capitalize(infectionModule.getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));
            return MinigameUtils.tabCompleteMatch(teams, args[0]);
        }
        return null;
    }
}
