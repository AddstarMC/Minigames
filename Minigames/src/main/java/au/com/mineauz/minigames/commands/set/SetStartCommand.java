package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetStartCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "start";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Sets the start point for the Minigame. Adding a player number sets that specific players start point.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> start [player number]",
                "/minigame set <Minigame> start <team colour> [player number]",
                "/minigame set <Minigame> start clear [team colour]"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set a players start point!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.start";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame, String @NotNull [] args) {
        Player player = (Player) sender;

        if (args == null) {
            minigame.setStartLocation(player.getLocation());
            sender.sendMessage(ChatColor.GRAY + "Starting position has been set for " + minigame.getName(false));
            return true;
        } else if (args.length == 1 && args[0].matches("[0-9]+")) {
            int position = Integer.parseInt(args[0]);

            if (position >= 1) {
                minigame.addStartLocation(player.getLocation(), position);
                sender.sendMessage(ChatColor.GRAY + "Starting position has been set for player " + args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "Error: Invalid starting position: " + args[0]);
                return false;
            }
            return true;
        } else if (args.length == 2 && TeamColor.matchColor(args[0]) != null && args[1].matches("[0-9]+")) {
            int position = Integer.parseInt(args[1]);
            Team team = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.matchColor(args[0]));
            if (team == null) {
                sender.sendMessage(ChatColor.RED + "No team color found by the name: " + args[0]);
                return false;
            }

            if (position >= 1) {
                team.addStartLocation(player.getLocation(), position);
                sender.sendMessage(ChatColor.GRAY + "Starting position for " +
                        team.getTextColor() + team.getDisplayName() + ChatColor.GRAY + " has been set for position " + position);
            } else {
                sender.sendMessage(ChatColor.RED + "Error: Invalid starting position: " + args[1]);
                return false;
            }
            return true;
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (args.length >= 2) {
                //check for team
                if (TeamColor.matchColor(args[1]) != null) {
                    Team team = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.matchColor(args[1]));
                    if (team == null) {
                        sender.sendMessage(ChatColor.RED + "No team color found by the name: " + args[1]);
                        return false;
                    }

                    team.getStartLocations().clear();
                    sender.sendMessage(ChatColor.GRAY + "Starting positions for " + team.getTextColor() + team.getDisplayName() + ChatColor.GRAY +
                            " have been cleared in " + minigame);
                } else {
                    sender.sendMessage(ChatColor.RED + "No team color found by the name: " + args[1]);
                    return false;
                }
            } else {
                minigame.getStartLocations().clear();
                sender.sendMessage(ChatColor.GRAY + "Starting positions have been cleared in " + minigame);
            }

            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        List<String> teams = new ArrayList<>(TeamsModule.getMinigameModule(minigame).getTeamsNameMap().size() + 1);
        if (args.length == 1) {
            TeamsModule.getMinigameModule(minigame).getTeamsNameMap().keySet().forEach(teamName -> teams.add(WordUtils.capitalize(teamName)));
            teams.add("Clear");
            return MinigameUtils.tabCompleteMatch(teams, args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
            return MinigameUtils.tabCompleteMatch(teams, args[1]);
        }
        return null;
    }
}
