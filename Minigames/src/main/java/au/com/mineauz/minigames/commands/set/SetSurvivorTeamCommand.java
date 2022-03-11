package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetSurvivorTeamCommand implements ICommand {
    @Override
    public String getName() {
        return "survivorteam";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"svteam"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Set which team color will represent the Survivor team in an Infection Minigame (Default: blue).";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> survivorteam <TeamColor>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the survivor team of an Infection Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.survivorteam";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("None")) {
                InfectionModule.getMinigameModule(minigame).setSurvivorTeam(null);
                sender.sendMessage(ChatColor.GRAY + "The survivor team of " + minigame + " has been set to none.");
            } else if (args[0].equalsIgnoreCase("Default")) {
                InfectionModule.getMinigameModule(minigame).setSurvivorTeam(WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));
                sender.sendMessage(ChatColor.GRAY + "The survivor team of " + minigame + " has been set to " +
                        WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));
            } else {
                if (TeamColor.matchColor(args[0]) == InfectionModule.getMinigameModule(minigame).getDefaultInfectedTeam() ||
                        TeamColor.matchColor(args[0]) == InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam() ||
                        TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.matchColor(args[0]))) {
                    InfectionModule.getMinigameModule(minigame).setSurvivorTeam(args[0]);
                    sender.sendMessage(ChatColor.GRAY + "The survivor team of " + minigame + " has been set to " + args[0] + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "There is no team for the color " + args[0]);
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame, String alias, String[] args) {
        if (args.length == 1) {
            List<String> teams = new ArrayList<>();
            for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                teams.add(t.getColor().toString().toLowerCase());
            }
            teams.add("none");
            teams.add("default");
            teams.add(WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultInfectedTeam().toString().toLowerCase().replace("_", " ")));
            teams.add(WordUtils.capitalize(InfectionModule.getMinigameModule(minigame).getDefaultSurvivorTeam().toString().toLowerCase().replace("_", " ")));
            return MinigameUtils.tabCompleteMatch(teams, args[0]);
        }
        return null;
    }
}
