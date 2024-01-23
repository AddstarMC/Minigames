package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScoreCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "score";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Gets, sets or adds to a player's or team's score. The Minigame name is only required if not assigning the score to a player.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame score get <Player or Team> [Minigame]",
                "/minigame score set <Player or Team> <NewScore> [Minigame]",
                "/minigame score add <Player or Team> [ExtraPoints] [Minigame]"
        };
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.score";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null && args.length >= 3) {
            MinigamePlayer ply = null;
            TeamColor color = TeamColor.matchColor(args[2]);
            Minigame mg = plugin.getMinigameManager().getMinigame(args[1]);

            if (color == null) {
                List<Player> plys = plugin.getServer().matchPlayer(args[2]);
                if (!plys.isEmpty()) {
                    ply = plugin.getPlayerManager().getMinigamePlayer(plys.get(0));
                } else {
                    sender.sendMessage(ChatColor.RED + "No player or team found by the name " + args[2]);
                    return true;
                }
            }

            if (mg == null) {
                sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[2]);
                return true;
            }


            switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "get" -> {
                    if (ply != null) {
                        if (ply.isInMinigame()) {
                            sender.sendMessage(ChatColor.GRAY + ply.getName() + "'s Score: " + ChatColor.GREEN + ply.getScore());
                        } else {
                            sender.sendMessage(ChatColor.RED + ply.getName() + " is not playing a Minigame!");
                        }
                    } else {
                        TeamsModule tmod = TeamsModule.getMinigameModule(mg);

                        if (mg.isTeamGame()) {
                            if (tmod.hasTeam(color)) {
                                Team t = tmod.getTeam(color);
                                sender.sendMessage(color.getColor() + t.getDisplayName() + ChatColor.GRAY + " score in " + mg.getName(false) + ": "
                                        + ChatColor.GREEN + t.getScore());
                            } else {
                                sender.sendMessage(ChatColor.RED + mg.getName(false) + " does not have a " + color.toString().toLowerCase() + " team.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + mg.getName(false) + " is not a team Minigame!");
                            return true;
                        }
                    }
                    return true;

                } // end case

                case "set" -> {
                    if (args.length >= 4) {
                        if (args[3].matches("^[+\\-]?[0-9]+$")) {
                            int score = Integer.parseInt(args[2]);

                            if (ply != null) {
                                if (ply.isInMinigame()) {
                                    ply.setScore(score);
                                    ply.getMinigame().setScore(ply, ply.getScore());
                                    sender.sendMessage(ChatColor.GRAY + ply.getName() + "'s score has been set to " + score);

                                    if (ply.getMinigame().getMaxScore() != 0 && score >= ply.getMinigame().getMaxScorePerPlayer()) {
                                        plugin.getPlayerManager().endMinigame(ply);
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + ply.getName() + " is not playing a Minigame!");
                                }
                            } else if (mg.isTeamGame()) {
                                TeamsModule tmod = TeamsModule.getMinigameModule(mg);
                                if (mg.hasPlayers()) {
                                    Team changedTeam;

                                    if (tmod.hasTeam(color)) {
                                        changedTeam = tmod.getTeam(color);
                                        changedTeam.setScore(score);
                                        sender.sendMessage(changedTeam.getTextColor() + changedTeam.getDisplayName() + ChatColor.GRAY + " score has been set to " + score);

                                        // check new score
                                        if (mg.getMaxScore() != 0 && score >= mg.getMaxScorePerPlayer()) {
                                            List<MinigamePlayer> winners = new ArrayList<>(changedTeam.getPlayers());
                                            List<MinigamePlayer> losers = new ArrayList<>(mg.getPlayers().size() - changedTeam.getPlayers().size());
                                            for (Team team : tmod.getTeams()) {
                                                if (team != changedTeam) {
                                                    losers.addAll(team.getPlayers());
                                                }
                                            }

                                            plugin.getPlayerManager().endMinigame(mg, winners, losers);
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + mg.getName(false) + " does not have a " + color.toString().toLowerCase() + " team.");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + mg.getName(false) + " has no players playing!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + mg.getName(false) + " is not a team Minigame!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Not enough arguments!");
                    }

                    return true;
                } // end case

                case "add" -> {
                    if (args.length >= 4) {
                        if (args[3].matches("^[+\\-]?[0-9]+$")) {
                            int score = Integer.parseInt(args[2]);

                            if (ply != null) {
                                if (ply.isInMinigame()) {
                                    ply.addScore(score);
                                    ply.getMinigame().setScore(ply, ply.getScore());
                                    sender.sendMessage(ChatColor.GRAY + "Added " + score + " to " + ply.getName() + "'s score, new score: " + ply.getScore());

                                    if (ply.getMinigame().getMaxScore() != 0 && ply.getScore() >= ply.getMinigame().getMaxScorePerPlayer()) {
                                        plugin.getPlayerManager().endMinigame(ply);
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + ply.getName() + " is not playing a Minigame!");
                                }
                            } else if (mg.isTeamGame()) {
                                TeamsModule tmod = TeamsModule.getMinigameModule(mg);
                                if (mg.hasPlayers()) {
                                    if (tmod.hasTeam(color)) {
                                        Team changedTeam = tmod.getTeam(color);
                                        if (changedTeam != null) {
                                            changedTeam.addScore(score);
                                            sender.sendMessage(ChatColor.GRAY + "Added " + score + " to " + changedTeam.getTextColor() + changedTeam.getDisplayName() +
                                                    ChatColor.GRAY + " score, new score: " + changedTeam.getScore());
                                        } else {
                                            sender.sendMessage(ChatColor.RED + mg.getName(false) + " does not have a " + color.toString().toLowerCase() + " team.");
                                            return true;
                                        }

                                        if (mg.getMaxScore() != 0 && changedTeam.getScore() >= mg.getMaxScorePerPlayer()) {
                                            List<MinigamePlayer> winners = new ArrayList<>(changedTeam.getPlayers());
                                            List<MinigamePlayer> losers = new ArrayList<>(mg.getPlayers().size() - changedTeam.getPlayers().size());
                                            for (Team team : tmod.getTeams()) {
                                                if (team != changedTeam) {
                                                    losers.addAll(team.getPlayers());
                                                }
                                            }
                                            plugin.getPlayerManager().endMinigame(mg, winners, losers);
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + mg.getName(false) + " does not have a " + color.toString().toLowerCase() + " team.");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + mg.getName(false) + " has no players playing!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + mg.getName(false) + " is not a team Minigame!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Not enough arguments!");
                    }

                    return true;
                } // end case
            } // end switch
        } else {
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @Nullable Minigame minigame,
                                                         @NotNull String @Nullable [] args) {
        if (args != null) {
            switch (args.length) {
                case 1 -> {
                    return MinigameUtils.tabCompleteMatch(List.of("get", "set", "add"), args[0]);
                }
                case 2 -> {
                    List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
                    return MinigameUtils.tabCompleteMatch(mgs, args[1]);
                }
                case 3 -> {
                    List<String> pt = new ArrayList<>(plugin.getServer().getOnlinePlayers().size());
                    for (Player pl : plugin.getServer().getOnlinePlayers()) {
                        pt.add(pl.getName());
                    }

                    Minigame mgm = plugin.getMinigameManager().getMinigame(args[1]);

                    if (mgm != null && mgm.isTeamGame()) {
                        pt.addAll(TeamsModule.getMinigameModule(mgm).getTeams().stream().map(t -> t.getColor().name()).toList());
                    }

                    return MinigameUtils.tabCompleteMatch(pt, args[2]);
                }
                case 4 -> {
                    if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")) {
                        if (args[3].matches("^[+\\-]?[0-9]+$")) {
                            List<String> numbers = new ArrayList<>(10);

                            for (int i = 0; i < 10; i++) {
                                numbers.add(args[3] + i);
                            }

                            return MinigameUtils.tabCompleteMatch(numbers, args[3]);
                        } // not a number
                    } // not add / set
                } // more than 4 arguments
            } // end switch
        } // args == null


        return null;
    }

}
