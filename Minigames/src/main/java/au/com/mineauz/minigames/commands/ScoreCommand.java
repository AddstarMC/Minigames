package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreCommand implements ICommand {

    @Override
    public String getName() {
        return "score";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Gets, sets or adds to a player's or team's score. The Minigame name is only required if not assigning the score to a player.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"get", "set", "add"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame score get <Player or Team> [Minigame]",
                "/minigame score set <Player or Team> <NewScore> [Minigame]",
                "/minigame score add <Player or Team> [ExtraPoints] [Minigame]"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to interact with a Minigames score!";
    }

    @Override
    public String getPermission() {
        return "minigame.score";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null && args.length >= 2) {
            MinigamePlayer ply = null;
            TeamColor color = TeamColor.matchColor(args[1]);

            if (color == null) {
                List<Player> plys = plugin.getServer().matchPlayer(args[1]);
                if (!plys.isEmpty()) {
                    ply = plugin.getPlayerManager().getMinigamePlayer(plys.get(0));
                } else {
                    sender.sendMessage(ChatColor.RED + "No player or team found by the name " + args[1]);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("get") && args.length >= 2) {
                if (ply != null) {
                    if (ply.isInMinigame()) {
                        sender.sendMessage(ChatColor.GRAY + ply.getName() + "'s Score: " + ChatColor.GREEN + ply.getScore());
                    } else {
                        sender.sendMessage(ChatColor.RED + ply.getName() + " is not playing a Minigame!");
                    }
                } else if (color != null) {
                    if (args.length >= 3) {
                        Minigame mg = null;
                        if (plugin.getMinigameManager().hasMinigame(args[2])) {
                            mg = plugin.getMinigameManager().getMinigame(args[2]);
                        } else {
                            sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[2]);
                            return true;
                        }

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
                    } else {
                        sender.sendMessage(ChatColor.RED + "This command requires a Minigame name as the last argument!");
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("set") && args.length >= 3) {

                int score = 0;

                if (args[2].matches("-?[0-9]+")) {
                    score = Integer.parseInt(args[2]);
                } else {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
                    return true;
                }

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
                } else if (color != null) {
                    if (args.length >= 4) {
                        Minigame mg = null;
                        if (plugin.getMinigameManager().hasMinigame(args[3])) {
                            mg = plugin.getMinigameManager().getMinigame(args[3]);
                        } else {
                            sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[2]);
                            return true;
                        }

                        TeamsModule tmod = TeamsModule.getMinigameModule(mg);

                        if (mg.isTeamGame() && mg.hasPlayers()) {
                            Team t = null;
                            if (tmod.hasTeam(color)) {
                                t = tmod.getTeam(color);
                                t.setScore(score);
                                sender.sendMessage(t.getChatColor() + t.getDisplayName() + ChatColor.GRAY + " score has been set to " + score);
                            } else {
                                sender.sendMessage(ChatColor.RED + mg.getName(false) + " does not have a " + color.toString().toLowerCase() + " team.");
                                return true;
                            }

                            if (mg.getMaxScore() != 0 && score >= mg.getMaxScorePerPlayer()) {
                                List<MinigamePlayer> w = new ArrayList<>(t.getPlayers());
                                List<MinigamePlayer> l = new ArrayList<>(mg.getPlayers().size() - t.getPlayers().size());
                                for (Team te : tmod.getTeams()) {
                                    if (te != t) {
                                        l.addAll(te.getPlayers());
                                    }
                                }
                                plugin.getPlayerManager().endMinigame(mg, w, l);
                            }
                        } else if (!mg.hasPlayers()) {
                            sender.sendMessage(ChatColor.RED + mg.getName(false) + " has no players playing!");
                        } else {
                            sender.sendMessage(ChatColor.RED + mg.getName(false) + " is not a team Minigame!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "This command requires a Minigame name as the last argument!");
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("add") && args.length >= 3) {
                int score = 0;

                if (args[2].matches("-?[0-9]+")) {
                    score = Integer.parseInt(args[2]);
                } else {
                    score = 1;
                }

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
                } else if (color != null) {
                    Minigame mg = null;
                    String mgName = null;

                    if (args.length == 4) {
                        mgName = args[3];
                    } else {
                        mgName = args[2];
                    }


                    if (plugin.getMinigameManager().hasMinigame(mgName)) {
                        mg = plugin.getMinigameManager().getMinigame(mgName);
                    } else {
                        sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + mgName);
                        return true;
                    }

                    TeamsModule tmod = TeamsModule.getMinigameModule(mg);

                    if (mg.isTeamGame() && mg.hasPlayers()) {
                        Team team = null;
                        if (tmod.hasTeam(color)) {
                            team = tmod.getTeam(color);
                            team.addScore(score);
                            sender.sendMessage(ChatColor.GRAY + "Added " + score + " to " + team.getChatColor() + team.getDisplayName() +
                                    ChatColor.GRAY + " score, new score: " + team.getScore());
                        } else {
                            sender.sendMessage(ChatColor.RED + mg.getName(false) + " does not have a " + color.toString().toLowerCase() + " team.");
                            return true;
                        }

                        if (mg.getMaxScore() != 0 && team.getScore() >= mg.getMaxScorePerPlayer()) {
                            List<MinigamePlayer> w = new ArrayList<>(team.getPlayers());
                            List<MinigamePlayer> l = new ArrayList<>(mg.getPlayers().size() - team.getPlayers().size());
                            for (Team te : tmod.getTeams()) {
                                if (te != team) {
                                    l.addAll(te.getPlayers());
                                }
                            }
                            plugin.getPlayerManager().endMinigame(mg, w, l);
                        }
                    } else if (!mg.hasPlayers()) {
                        sender.sendMessage(ChatColor.RED + mg.getName(false) + " has no players playing!");
                    } else {
                        sender.sendMessage(ChatColor.RED + mg.getName(false) + " is not a team Minigame!");
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("get;set;add"), args[0]);
        } else if (args.length == 2) {

            List<String> pt = new ArrayList<>(plugin.getServer().getOnlinePlayers().size() + 2);
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                pt.add(pl.getName());
            }
            pt.add("red");
            pt.add("blue");

            return MinigameUtils.tabCompleteMatch(pt, args[1]);
        }
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
