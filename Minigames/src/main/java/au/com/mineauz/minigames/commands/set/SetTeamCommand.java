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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetTeamCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "team";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Adds, removes and modifies a team for a specific Minigame.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return new String[]{"add", "rename", "remove", "list", "maxplayers"};
    }

    @Override
    public Component getUsage() {
        return new String[]{"/minigame set <Minigame> team add <TeamColor> [Display Name]",
                "/minigame set <Minigame> team rename <TeamColor> <Display Name>",
                "/minigame set <Minigame> team remove <TeamColor>",
                "/minigame set <Minigame> team maxplayers <TeamColor> <number>"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to modify Minigame teams.";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.team";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            TeamsModule tmod = TeamsModule.getMinigameModule(minigame);
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length >= 2) {
                    TeamColor col = TeamColor.matchColor(args[1]);
                    StringBuilder name = null;
                    if (col != null) {
                        if (args.length > 2) {
                            name = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                name.append(args[i]);
                                if (i != args.length - 1)
                                    name.append(" ");
                            }
                        }
                        if (name != null) {
                            Team team = tmod.addTeam(col, name.toString());
                            sender.sendMessage(ChatColor.GRAY + "Added " + WordUtils.capitalize(team.getColor().toString()) +
                                    " team to " + minigame.getName(false) + " with the display name " + name);
                        } else {
                            Team team = tmod.addTeam(col);
                            sender.sendMessage(ChatColor.GRAY + "Added " + WordUtils.capitalize(team.getDisplayName()) +
                                    " to " + minigame.getName(false));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid team color! Valid options:");
                        sender.sendMessage(TeamColor.validColorNamesComp());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Valid team color options:");
                    sender.sendMessage(TeamColor.validColorNamesComp());
                }
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                List<String> teams = new ArrayList<>(tmod.getTeams().size());
                for (Team t : tmod.getTeams()) {
                    teams.add(t.getTextColor() + t.getColor().toString() + ChatColor.GRAY +
                            "(" + t.getTextColor() + t.getDisplayName() + ChatColor.GRAY + ")");
                }
                StringBuilder teamsString = new StringBuilder();
                for (String t : teams) {
                    teamsString.append(t);
                    if (!t.equals(teams.get(teams.size() - 1)))
                        teamsString.append(", ");
                }
                sender.sendMessage(ChatColor.GRAY + "List of teams in " + minigame.getName(false) + ":");
                sender.sendMessage(teamsString.toString());
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length >= 2) {
                    TeamColor col = TeamColor.matchColor(args[1]);
                    if (col != null) {
                        if (tmod.hasTeam(col)) {
                            tmod.removeTeam(col);
                            sender.sendMessage(ChatColor.GRAY + "Removed " + WordUtils.capitalize(col.toString()) + " from " + minigame.getName(false));
                        } else {
                            sender.sendMessage(ChatColor.RED + minigame.getName(false) + " does not have the team " + WordUtils.capitalize(col.toString()));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid team color! Valid options:");

                        String cols = tmod.getTeams().stream().map(t -> t.getColor().toString()).collect(Collectors.joining("<gray>, </gray>"));

                        sender.sendMessage(cols);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Valid teams:");

                    String cols = tmod.getTeams().stream().map(t -> t.getColor().toString()).collect(Collectors.joining("<gray>, </gray>"));

                    sender.sendMessage(cols);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("rename")) {
                if (args.length > 2) {
                    TeamColor col = TeamColor.matchColor(args[1]);
                    StringBuilder name = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        name.append(args[i]);
                        if (i != args.length - 1)
                            name.append(" ");
                    }
                    if (col != null) {
                        if (tmod.hasTeam(col)) {
                            tmod.getTeam(col).setDisplayName(name.toString());
                            sender.sendMessage(ChatColor.GRAY + "Set " + WordUtils.capitalize(col.toString()) + " display name to " + name + " for " + minigame.getName(false));
                        } else {
                            sender.sendMessage(ChatColor.RED + minigame.getName(false) + " does not have the team " + WordUtils.capitalize(col.toString()));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid team color! Valid options:");

                        String cols = tmod.getTeams().stream().map(t -> t.getColor().toString()).collect(Collectors.joining("<gray>, </gray>"));

                        sender.sendMessage(cols);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Valid teams:");

                    String cols = tmod.getTeams().stream().map(t -> t.getColor().toString()).collect(Collectors.joining("<gray>, </gray>"));

                    sender.sendMessage(cols);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("maxplayers") && args.length == 3) {
                if (TeamColor.matchColor(args[1]) != null) {
                    TeamColor col = TeamColor.matchColor(args[1]);
                    if (tmod.hasTeam(col)) {
                        if (args[2].matches("[0-9]+")) {
                            int val = Integer.parseInt(args[2]);
                            tmod.getTeam(col).setMaxPlayers(Integer.parseInt(args[2]));
                            sender.sendMessage(ChatColor.GRAY + "Maximum players for the team " + tmod.getTeam(col).getDisplayName() + " has been set to " + val);
                        } else {
                            sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "There is no " + WordUtils.capitalize(col.toString()) + " Team in " + minigame);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not a valid team color!");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("add", "rename", "remove", "list", "maxplayers"), args[0]);
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                return MinigameUtils.tabCompleteMatch(new ArrayList<>(TeamColor.validColorNames()), args[1]);
            } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rename")) {
                return MinigameUtils.tabCompleteMatch(TeamsModule.getMinigameModule(minigame).getTeams().stream().map(t -> t.getColor().toString()).toList(), args[1]);
            } else if (args[0].equalsIgnoreCase("maxplayers")) {
                List<String> cols = new ArrayList<>();
                for (Team t : TeamsModule.getMinigameModule(minigame).getTeams())
                    cols.add(t.getColor().toString());
                return MinigameUtils.tabCompleteMatch(cols, args[1]);
            }
        }
        return null;
    }

}
